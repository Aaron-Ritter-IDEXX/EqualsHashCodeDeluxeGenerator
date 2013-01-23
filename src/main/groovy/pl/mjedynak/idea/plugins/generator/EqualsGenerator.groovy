package pl.mjedynak.idea.plugins.generator

import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.*
import org.jetbrains.annotations.NotNull

class EqualsGenerator {

    PsiMethod equalsMethod(@NotNull List<PsiField> equalsPsiFields, PsiClass psiClass, String equalsMethodName) {
        if (!equalsPsiFields.isEmpty()) {
            PsiElementFactory factory = getFactory(equalsPsiFields[0])
            StringBuilder methodText = new StringBuilder()
            methodText << '@Override public boolean equals(Object obj) {'
            methodText << ' if (this == obj) {return true;}'
            methodText << ' if (obj == null || getClass() != obj.getClass()) {return false;}'
            if (isSubclass(psiClass)) {
                methodText << ' if (!super.equals(obj)) {return false;}'
            }
            methodText << " final ${psiClass.name} other = (${psiClass.name}) obj;"
            methodText << ' return '
            equalsPsiFields.eachWithIndex { field, index ->
                methodText <<  "Objects.${equalsMethodName}(this.${field.name}, other.${field.name})"
                if (isNotLastField(equalsPsiFields, index)) {
                    methodText << ' && '
                }
            }
            methodText << ';}'
            factory.createMethodFromText(methodText.toString(), null, LanguageLevel.JDK_1_6)
        }
    }

    private boolean isSubclass(PsiClass psiClass) {
        psiClass.extendsList?.referencedTypes?.length > 0
    }

    private boolean isNotLastField(@NotNull List<PsiField> equalsPsiFields, int index) {
        index < equalsPsiFields.size() - 1
    }

    private PsiElementFactory getFactory(PsiField psiField) {
        JavaPsiFacade.getInstance(psiField.project).elementFactory
    }
}


