package com.bdd.social

import android.content.ComponentName
import android.widget.EditText
import com.bdd.social.api.ApiInterface
import com.bdd.social.model.User
import hkhc.electricspock.ElectricSpecification
import io.reactivex.Observable
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import spock.lang.Unroll

@Config(application = TestApp)
class LoginActivitySpec extends ElectricSpecification {

    private ApiInterface mockApi
    private TestApp testApp

    void setup() {
        testApp = RuntimeEnvironment.application as TestApp
        mockApi = Mock(ApiInterface)
        this.testApp.apiInterface = this.mockApi
    }

    void cleanup() {
        testApp.@apiInterface = null
        testApp.@feather = null
    }

    def "should load activity"() {
        expect: "User can see Login screen"
        Robolectric.setupActivity(LoginActivity) != null
    }

    def "as a user I should be able to input login and password"() {
        when: "I open Login screen"
        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        then: "I can see login and password inputs"
        loginActivity.findViewById(R.id.loginEditText) as EditText
        loginActivity.findViewById(R.id.passwordEditText) as EditText

        and: "Login button"
        loginActivity.findViewById(R.id.loginButton) != null
    }

    @Unroll
    def "should not allow input #name"(email, pass, errorMessage, name) {
        given:
        def loginActivity = 'open login screen'()

        when: "I input invalid email"
        loginActivity.loginEditText.setText(email)
        loginActivity.passwordEditText.setText(pass)

        and: "I submit credentials"
        loginActivity.loginButton.performClick()

        then: "I see validation error"
        ShadowToast.getTextOfLatestToast() == errorMessage

        where:
        email     | pass  | errorMessage
        ""        | "123" | "Please, input valid email"
        "1"       | "123" | "Please, input valid email"
        "1@1.com" | ""    | "Please, input valid password"

        name << [
                "empty email",
                "malformed email",
                "empty pass"
        ]

    }

    def "should show server error"() {
        given: "I open Login screen"
        def loginActivity = 'open login screen'()
        def errorMessage = "User not found!"

        when: "I submit valid credentials"
        loginActivity.loginEditText.setText("email@email.com")
        loginActivity.passwordEditText.setText("1234")

        and: "Server responds with an error"
        mockApi.login(_, _) >> Observable.error(new RuntimeException(errorMessage))
        loginActivity.loginButton.performClick()

        then: "I can see server error"
        ShadowToast.getTextOfLatestToast() == errorMessage
    }

    def "should open feed screen and close"() {
        given: "I open Login screen"
        def loginActivity = 'open login screen'()

        when: "I submit valid credentials"
        loginActivity.loginEditText.setText("email@email.com")
        loginActivity.passwordEditText.setText("1234")
        loginActivity.loginButton.performClick()

        then: "Server is called"
        1 * mockApi.login("email@email.com", "1234") >> Observable.just(new User())

        and: "Next screen is open"
        Shadows.shadowOf(loginActivity).peekNextStartedActivity()
            .component == new ComponentName(loginActivity, FeedActivity)

        and: "Login screen is closed"
        loginActivity.isFinishing()
    }

    private LoginActivity 'open login screen'() {
        Robolectric.buildActivity(LoginActivity)
                .setup().visible().get()
    }
}