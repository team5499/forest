package org.team5499.dashboard

import spark.ModelAndView
import spark.TemplateEngine

import com.google.common.base.Charsets
import com.hubspot.jinjava.Jinjava
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.interpret.Context
import com.hubspot.jinjava.lib.filter.Filter
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition
import com.hubspot.jinjava.lib.tag.Tag
import com.hubspot.jinjava.loader.ResourceLocator

class ModifiedJinjavaEngine : TemplateEngine {

    private val jinjava: Jinjava
    private val context: Context

    constructor(jinjavaConfig: JinjavaConfig, resourceLocator: ResourceLocator) {
        jinjava = Jinjava(jinjavaConfig)
        jinjava.setResourceLocator(resourceLocator)
        context = jinjava.getGlobalContext()
    }

    fun registerTag(tag: Tag) {
        context.registerTag(tag)
    }

    fun registerFilter(filter: Filter) {
        context.registerFilter(filter)
    }

    fun registerFunction(function: ELFunctionDefinition) {
        context.registerFunction(function)
    }

    @Override
    override fun render(modelAndView: ModelAndView): String {
        val model: Any = modelAndView.getModel()
        if (model is Map<*, *>) {
            var template: String = jinjava.getResourceLocator().getString(modelAndView.getViewName(),
                                                                            Charsets.UTF_8,
                                                                            jinjava.newInterpreter())
            // try {
            //     template = Resources.toString(Resources.getResource(modelAndView.getViewName()), Charsets.UTF_8)
            // } catch (IOException ignored) {
            // }
            return jinjava.render(template, model as Map<String, Any>)
        } else {
            throw IllegalArgumentException("modelAndView.getModel() must return a java.util.Map")
        }
    }
}
