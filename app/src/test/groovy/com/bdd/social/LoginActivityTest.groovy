package com.bdd.social

import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric

class LoginActivityTest extends ElectricSpecification {

    def "should load activity"() {
        expect: "User can see Login screen"
        Robolectric.setupActivity(LoginActivity) != null
    }

}