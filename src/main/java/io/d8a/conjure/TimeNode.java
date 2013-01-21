package io.d8a.conjure;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Map;

public class TimeNode implements SampleNode {
    private final Clock clock;
    private final DateNodeFormatter format;

    public TimeNode(final Clock clock, final String format){
        this(clock, format, DateTimeZone.getDefault().getID());
    }

    public TimeNode(final Clock clock, final String format, final String timeZone){
        this.clock = clock;
        if("millis".equals(format)){
            this.format = new DateNodeFormatter() {
                public String print(long millis) {
                    return ""+clock.currentTimeMillis();
                }
            };
        }else{
            final DateTimeFormatter f = DateTimeFormat.forPattern(format).withZone(DateTimeZone.forID(timeZone));
            this.format = new DateNodeFormatter() {
                public String print(long millis) {
                    return f.print(millis);
                }
            };
        }
    }

    private interface DateNodeFormatter {
        public String print(long millis);
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        buff.append(format.print(clock.currentTimeMillis()));
        return buff;
    }

    public static SampleNode createNode(Map config, ConjureTemplate template) {
        String format = (String)config.get("format");
        if(format == null){
            format = "millis";
        }
        String timezone = (String) config.get("timezone");
        Jitter jitter = createJitter(config, template);
        Clock clock = new JitterClock(template.getClock(), jitter);
        if(timezone != null){
            return new TimeNode(clock, format, timezone);
        }
        return new TimeNode(clock, format);
    }

    private static Jitter createJitter(Map config, ConjureTemplate template) {
        if(config.containsKey("min") || config.containsKey("max")){
            return MinMaxNode.createNode(config).getMinmax();
        }
        if(config.containsKey("minmaxRef")){
            final String jitterRefName = (String) config.get("minmaxRef");
            MinMaxNode minmaxNode = (MinMaxNode) template.getNode(jitterRefName);
            if(minmaxNode != null){
                return minmaxNode.getMinmax();
            }
            return new LazyJitter(jitterRefName, template);
        }
        return Jitter.NO_JITTER;
    }

    private static class LazyJitter implements Jitter{
        private String refName;
        private ConjureTemplate template;
        private MinMax minmax;

        private LazyJitter(String refName, ConjureTemplate template) {
            this.refName = refName;
            this.template = template;
        }

        @Override
        public long nextValue() {
            if(minmax == null){
                MinMaxNode myNode = (MinMaxNode) template.getNode(refName);
                if(myNode == null){
                    throw new IllegalArgumentException("Node '"+refName+"' not found in the template.");
                }
                minmax = myNode.getMinmax();
            }
            return minmax.nextValue();
        }
    }
}
