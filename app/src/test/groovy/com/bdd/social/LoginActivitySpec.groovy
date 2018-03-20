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

@Config(application = TestApp)
class LoginActivitySpec extends ElectricSpecification {

    private TestApp testApp
    private ApiInterface mockApi

    void setup() {
        mockApi = Mock(ApiInterface)
        testApp = RuntimeEnvironment.application as TestApp
        testApp.apiInterface = mockApi
    }

    void cleanup() {
        // Clean up Feather to invalidate dependency graph.
        testApp.@apiInterface = null
        testApp.@feather = null
    }

    def "should load activity"() {
        expect: "User can see Login screen"
        Robolectric.setupActivity(LoginActivity) != null
    }

    def "as a user I should be able to input login and password"() {
        when: "I open Login scree"
        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().get()

        then: "I can see login and password inputs"
        loginActivity.findViewById(R.id.loginEditText) as EditText
        loginActivity.findViewById(R.id.passwordEditText) as EditText

        and: "Login button"
        loginActivity.findViewById(R.id.loginButton) != null
    }

    def "should validate login input"() {
        given: "I open login screen"
        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        when: "I input empty login"
        loginActivity.loginEditText.setText("")
        loginActivity.passwordEditText.setText("password")

        and: "I submit data"
        loginActivity.loginButton.performClick()

        then: "I should see a corresponding error"
        ShadowToast.getTextOfLatestToast().contains("Login may not be empty")

    }

    def "should validate password input"() {
        given: "I open login screen"
        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        when: "I input login"
        loginActivity.loginEditText.setText("user login")

        and: "empty password"
        loginActivity.passwordEditText.setText("")

        and: "I submit data"
        loginActivity.loginButton.performClick()

        then: "I should see a corresponding error"
        ShadowToast.getTextOfLatestToast().contains("Password may not be empty")

    }

    def "should show error from server"() {
        given: "I am on login screen"
        def errorMessage = "Invalid login or password"
        mockApi.login("some", "login") >> { Observable.error(new RuntimeException(errorMessage)) }

        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        when: "I input login and password"
        loginActivity.loginEditText.text = "some"
        loginActivity.passwordEditText.text = "login"

        and: "I submit data"
        loginActivity.loginButton.performClick()

        then: "Corresponding message is shown"
        ShadowToast.getTextOfLatestToast() == errorMessage
    }

    def "should open Feed and finish when logged in"() {
        given: "I am on login screen"
        mockApi.login("login", "password") >> { Observable.just(new User()) }

        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        when: "I input login and password"
        loginActivity.loginEditText.text = "login"
        loginActivity.passwordEditText.text = "password"

        and: "I submit data"
        loginActivity.loginButton.performClick()

        then: "Feed screen is opened"
        Shadows.shadowOf(loginActivity).peekNextStartedActivity().component == new ComponentName(loginActivity, FeedActivity)

        and:
        loginActivity.isFinishing()
    }

}