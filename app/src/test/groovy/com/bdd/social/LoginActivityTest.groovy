package com.bdd.social

import android.widget.EditText
import com.bdd.social.api.ApiInterface
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(application = TestApp)
class LoginActivityTest extends ElectricSpecification {

    void setup() {
        def testApp = RuntimeEnvironment.application as TestApp
        def mockApi = Mock(ApiInterface)
        testApp.apiInterface = mockApi
    }

    def "should load activity"() {
        expect: "User can see Login screen"
        Robolectric.setupActivity(LoginActivity) != null
    }

    def "as a user I should be able to input login and password"() {
        when: "I open Login scree"
        def loginActivity = Robolectric.buildActivity(LoginActivity).setup().visible().get()

        then: "I can see login and password inputs"
        loginActivity.findViewById(R.id.loginEditText) as EditText
        loginActivity.findViewById(R.id.passwordEditText) as EditText

        and: "Login button"
        loginActivity.findViewById(R.id.loginButton) != null
    }
}