package it.unibo.jakta

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withAbstractModifier
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.ext.list.withParentInterfaceOf
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FreeSpec
import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEvent
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import kotlinx.serialization.Serializable

class JaktaConventionTest :
    FreeSpec({
        "implementation classes should have Impl suffix and reside in impl package" {
            projectScope
                .classes()
                .withNameEndingWith("Impl")
                .assertTrue { it.resideInPackage("..impl..") }
        }

        "classes with the Impl suffix that reside in impl package that are not open should have internal modifier" {
            projectScope
                .classes()
                .withPackage("..impl..")
                .withNameEndingWith("Impl")
                .filterNot { it.hasModifier(KoModifier.OPEN) }
                .assertTrue { it.hasModifier(KoModifier.INTERNAL) }
        }

        "classes implementing the ScopeBuilder interface must have the suffix Scope" {
            projectScope
                .classes()
                .withParentInterfaceOf(ScopeBuilder::class)
                .assertTrue { it.hasNameEndingWith("Scope") }
        }

        "classes extending the DefaultTermVisitor must have the suffix Visitor" {
            projectScope
                .classes()
                .withParentInterfaceOf(DefaultTermVisitor::class)
                .assertTrue { it.hasNameEndingWith("Visitor") }
        }

        "classes extending JaktaLogEvent must be serializable" {
            projectScope
                .classes()
                .withParentInterfaceOf(JaktaLogEvent::class)
                .assertTrue { it.hasAnnotationOf(Serializable::class) }
        }

        "abstract classes must have the prefix Abstract" {
            projectScope
                .classes()
                .withAbstractModifier()
                .assertTrue { it.hasNameStartingWith("Abstract") }
        }
    }) {
    companion object {
        val projectScope = Konsist.scopeFromProduction()
    }
}
