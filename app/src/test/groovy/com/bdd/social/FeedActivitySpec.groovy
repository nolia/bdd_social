package com.bdd.social

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.bdd.social.api.ApiInterface
import com.bdd.social.model.Message
import com.bdd.social.model.User
import hkhc.electricspock.ElectricSpecification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Spec for Feed screen
 */
@Config(application = TestApp)
class FeedActivitySpec extends ElectricSpecification {

    private TestApp testApp
    private ApiInterface mockApi
    private Subject messageSubject
    private List messageList = []

    void setup() {
        mockApi = Mock(ApiInterface)
        testApp = RuntimeEnvironment.application as TestApp
        testApp.apiInterface = mockApi

        messageSubject = PublishSubject.create()
        mockApi.getNewMessageChannel() >> this.messageSubject
        mockApi.getMessages() >> { Observable.just(messageList) }
    }

    void cleanup() {
        // Clean up Feather to invalidate dependency graph.
        testApp.@apiInterface = null
        testApp.@feather = null
    }

    def "should display messages"() {
        given: "There are messaged on server"
        messageList = [
                new Message(new User(), "Hello, world!")
        ]

        when: "I open Feed screen"
        def feedActivity = Robolectric.buildActivity(FeedActivity).setup().visible().get()

        then: "I can see messages on screen"
        def recyclerView = feedActivity.recyclerView
        recyclerView.getAdapter().getItemCount() == messageList.size()

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            messageIsDisplayed(recyclerView.getChildAt(i), messageList[i])
        }
    }

    def "should display new incoming message"() {
        given: "I an on the Feed screen"
        def feedActivity = Robolectric.buildActivity(FeedActivity).setup().visible().get()

        when: "Server sends a new message"
        def message = new Message(new User("author@email.com", "Message Author"), "Message Text")
        messageSubject.onNext(message)

        then: "Message is added to message list on screen"
        def lastMessageIndex = feedActivity.recyclerView.getAdapter().getItemCount() - 1
        def itemView = feedActivity.recyclerView.layoutManager.findViewByPosition(lastMessageIndex)

        messageIsDisplayed(itemView, message)
    }

    def "should send message to server"() {
        given: "I an on the Feed screen"
        def feedActivity = Robolectric.buildActivity(FeedActivity).setup().visible().get()
        def messageText = "Hello, message!"

        when: "I input a message"
        feedActivity.inputText.text = messageText

        and: "I submit it"
        feedActivity.sendButton.performClick()

        then: "Message is sent to the server"
        1 * mockApi.sendMessage(_, messageText) >> Observable.just(new Message(new User(), messageText))

        and: "Text input is reset"
        TextUtils.isEmpty(feedActivity.inputText.text)
    }

    private static boolean messageIsDisplayed(View itemView, Message message) {
        return ((itemView.findViewById(R.id.author) as TextView).text == message.author.name) &&
                ((itemView.findViewById(R.id.text) as TextView).text == message.text)
    }
}
