package log4j2_clojure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Symbol;
import clojure.lang.IDeref;
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


@Plugin(name="CljFn", category="Core", elementType="appender", printObject=true)
public class CljFnAppender extends AbstractAppender {
  public IFn logFn;

  public class LogEventWrapper implements IDeref {
    private LogEvent logEvent;
    private Layout logLayout;

    public LogEventWrapper(LogEvent event, Layout layout) {
      this.logEvent = event.toImmutable();
      this.logLayout = layout;
    }

    public String toString() {
      return this.logLayout.toSerializable(this.logEvent).toString();
    }

    public Object getBean() {
      IFn bean = Clojure.var("clojure.core", "bean");
      return bean.invoke(this.logEvent);
    }

    public Object deref() {
      return this.logEvent;
    }
  }

  private CljFnAppender(String name,
                        Filter filter,
                        Layout<? extends Serializable> layout,
                        boolean ignoreExceptions,
                        Property[] properties) {
    super(name, filter, layout, ignoreExceptions, properties);
  }

  @PluginFactory
  public static CljFnAppender createAppender(@PluginElement("Layout") Layout<? extends Serializable> layout,
                                             @PluginElement("Filters") final Filter filter,
                                             @PluginAttribute("name") final String name,
                                             @PluginAttribute("ns") final String ns,
                                             @PluginAttribute("fn") final String fn) {

    if (name == null) {
      LOGGER.error("No name provided for CljFnAppender");
      return null;
    }

    if (ns == null) {
      LOGGER.error("No ns provided for CljFnAppender");
      return null;
    }

    if (fn == null) {
      LOGGER.error("No fn provided for CljFnAppender");
      return null;
    }

    if (layout == null) {
      LOGGER.error("Pattern layout not provided");
      return null;
    }

    CljFnAppender instance =  new CljFnAppender(name, filter, layout, true, null);

    Symbol sns = Symbol.intern(ns);
    Symbol sfn = Symbol.intern(fn);

    IFn require = Clojure.var("clojure.core", "require");
    require.invoke(sns);

    instance.logFn = (IFn)Clojure.var(sns, sfn);
    return instance;
  }

  public void append(LogEvent logEvent) {
    if (this.logFn != null) {
      this.logFn.invoke(new LogEventWrapper(logEvent, getLayout()));
    }
  }
}
