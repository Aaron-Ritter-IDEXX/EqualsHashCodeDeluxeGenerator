package pl.mjedynak.idea.plugins.psi

import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiMethodImpl
import spock.lang.Specification

class HashCodeFinderTest extends Specification {

    HashCodeFinder finder = new HashCodeFinder()

    PsiClass psiClass = Mock()
    PsiMethodImpl method = Mock()
    PsiType returnType = Mock()

    def setup() {
        psiClass.findMethodsByName("hashCode", false) >> [method]
    }

    def "should determine that given class has correct 'hashCode' method"() {
        psiClass.findMethodsByName("hashCode", false) >> [Mock(PsiMethodImpl), method]
        isPublic()
        returnsInt()
        hasNoParameters()

        when:
        boolean result = finder.hasHashCodeMethod(psiClass)

        then:
        result == true
    }

    def "should determine that method is not 'hashCode' if it is not public"() {
        isPrivate()
        returnsInt()
        hasNoParameters()

        when:
        boolean result = finder.hasHashCodeMethod(psiClass)

        then:
        result == false
    }

    def "should determine that method is not 'hashCode' if it does not return boolean"() {
        isPublic()
        returnsObject()
        hasNoParameters()

        when:
        boolean result = finder.hasHashCodeMethod(psiClass)

        then:
        result == false
    }

    def "should determine that method is not 'hashCode' if it has parameters parameter"() {
        isPublic()
        returnsInt()
        hasParameters()

        when:
        boolean result = finder.hasHashCodeMethod(psiClass)

        then:
        result == false
    }

    def "should determine that method is not 'hashCode' if it is static"() {
        isPublic()
        isStatic()
        returnsInt()
        hasNoParameters()

        when:
        boolean result = finder.hasHashCodeMethod(psiClass)

        then:
        result == false
    }

    private void isPublic() {
        method.hasModifierProperty(PsiModifier.PUBLIC) >> true
    }

    private void isPrivate() {
        method.hasModifierProperty(PsiModifier.PRIVATE) >> true
    }

    private void isStatic() {
        method.hasModifierProperty(PsiModifier.STATIC) >> true
    }

    private void returnsInt() {
        method.getReturnType() >> returnType
        returnType.equalsToText("int") >> true
    }

    private void returnsObject() {
        method.getReturnType() >> returnType
        returnType.equalsToText("Object") >> true
    }

    private void hasNoParameters() {
        PsiParameterList psiParameterList = Mock()
        psiParameterList.getParameters() >> []
        method.getParameterList() >> psiParameterList
    }

    private void hasParameters() {
        PsiParameterList psiParameterList = Mock()
        psiParameterList.getParameters() >> [Mock(PsiParameter)]
        method.getParameterList() >> psiParameterList
    }

}
