package com.bdd.social

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.bdd.social.api.ApiInterface
import com.bdd.social.model.Message
import com.bdd.social.model.User
import hkhc.electricspock.ElectricSpecification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowToast;

class FeedActivitySpec extends ElectricSpecification {

    private ApiInterface mockApi
    private TestApp testApp

    private Observable<List> messageObservable = Observable.just([])
    private PublishSubject<Message> newMessageChannel = PublishSubject.create()

    void setup() {
        testApp = RuntimeEnvironment.application as TestApp
        mockApi = Mock(ApiInterface)
        mockApi.getMessages() >> { messageObservable }
        mockApi.getNewMessageChannel() >> { newMessageChannel }

        this.testApp.apiInterface = this.mockApi
    }

    void cleanup() {
        testApp.@apiInterface = null
        testApp.@feather = null
    }

    def "should load messages"(List<Message> messages) {
        given: "There are messages on server"
        messageObservable = Observable.just(messages)

        when: "I open Feed screen"
        def feedActivity = Robolectric.buildActivity(FeedActivity)
                .setup().visible().get()

        then: "I should see messages from server"
        feedActivity.recyclerView.adapter.itemCount == messages.size()
        def layoutManager = feedActivity.recyclerView.layoutManager as LinearLayoutManager
        def first = layoutManager.findFirstVisibleItemPosition()
        def last = layoutManager.findLastVisibleItemPosition()

        for (int i = first; i <= last; i++) {
            'message is displayed'(layoutManager.findViewByPosition(i), messages[i])
        }

        where:
        messages << [
                [new Message(new User("user1@email.com", "1"), "Hello")],
                [
                        new Message(new User("user1@email.com", "1"), "Hello"),
                        new Message(new User("user2@email.com", "2"), "world"),
                        new Message(new User("user3@email.com", "3"), "1"),
                ],
                aLotOfMessages()
        ]
    }

    def "should display error from server"() {
        given: "Server responds with an error"
        def errorMessage = "Internal error!"
        messageObservable = Observable.error(new RuntimeException(errorMessage))

        when: "I open Feed screen"
        Robolectric.buildActivity(FeedActivity)
                .setup().visible().get()


        then: "I should see server error"
        ShadowToast.getTextOfLatestToast() == errorMessage

    }

    def "should be able to post new message to feed"() {
        given: "I am on the Feed screen"
        def feedActivity = Robolectric.buildActivity(FeedActivity)
                .setup().visible().get()

        when: "I submit a message"
        feedActivity.inputText.setText("Hello!")
        feedActivity.sendButton.performClick()

        then: "Message is sent to the server"
        1 * mockApi.sendMessage(_, _) >> Observable.just(new Message(new User(), "Hello!"))
    }

    def "should show new messages on feed"() {
        given: "I am on the Feed screen"
        def message = new Message(new User("batman@cave.com", "Batman"), "new message!")

        def feedActivity = Robolectric.buildActivity(FeedActivity)
                .setup().visible().get()

        when: "New message arrives from server"
        newMessageChannel.onNext(message)

        then: "I see this message at the end of the feed"
        def lastOne = feedActivity.recyclerView.adapter.itemCount - 1
        def view = feedActivity.recyclerView.layoutManager.findViewByPosition(lastOne)
        'message is displayed'(view, message)

    }

    private List<Message> aLotOfMessages() {
        def list = []
        for (int i = 0; i < 1000; i++) {
            list.add(new Message(
                    new User("user$i@email.com", "User $i"), "Hello from $i!")
            )
        }
        return list
    }

    private boolean 'message is displayed'(View view, Message message) {
        return getViewText(view, R.id.author) == message.author.name &&
                getViewText(view, R.id.text) == message.text &&
                getViewText(view, R.id.date) == message.date.toString()
    }

    private String getViewText(View view, int author) {
        (view.findViewById(author) as TextView).getText()
                .toString()
    }
}