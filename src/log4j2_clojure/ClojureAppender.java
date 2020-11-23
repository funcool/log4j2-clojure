package log4j2_clojure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;


@Plugin(name="Clojure", category="Core", elementType="appender", printObject=true)
public class ClojureAppender extends AbstractAppender {
  public IFn logFn;


  private ClojureAppender(String name,
                          Filter filter,
                          Layout<? extends Serializable> layout,
                          boolean ignoreExceptions,
                          Property[] properties) {
    super(name, filter, layout, ignoreExceptions, properties);
  }

  @PluginFactory
  public static ClojureAppender createAppender(@PluginElement("Layout") Layout<? extends Serializable> layout,
                                               @PluginElement("Filters") final Filter filter,
                                               @PluginAttribute("name") final String name,
                                               @PluginAttribute("fn") final String cljfn) {

    if (name == null) {
      LOGGER.error("No name provided for ClojureAppender");
      return null;
    }

    if (cljfn == null) {
      LOGGER.error("No fn provided for ClojureAppender");
      return null;
    }

    if (layout == null) {
      LOGGER.error("Pattern layout not provided");
      return null;
    }

    ClojureAppender instance =  new ClojureAppender(name, filter, layout, true, null);
    IFn resolve = Clojure.var("clojure.core", "requiring-resolve");
    instance.logFn = (IFn)resolve.invoke(Clojure.read(cljfn));
    return instance;
  }

  public void append(LogEvent logEvent) {
    if (this.logFn != null) {
      String logLine = getLayout().toSerializable(logEvent).toString();
      this.logFn.invoke(logLine);
    }
  }
}
