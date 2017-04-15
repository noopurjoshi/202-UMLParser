import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.SourceLocation;

@Aspect
public class UmlSequenceRelationship {

    private static List<TracingEvent> messages = new ArrayList<>();
    
    // methods in classes
    @Before("within(*.*) && call(* *.*.*(..)) && !withincode(* *.*.main(..)) && !within(UmlSequenceRelationship)")
    public void before3(JoinPoint thisJoinPoint) {
        traceEntry(getThis(thisJoinPoint), getTarget(thisJoinPoint), thisJoinPoint.getSignature(), thisJoinPoint.getSourceLocation(), thisJoinPoint.getArgs());
    }

    private void traceEntry(final String aThis, final String target, final Signature signature, final SourceLocation sourceLocation, final Object[] args) {
        if(aThis != null && target != null) {
            String message = aThis + " -> " + target + " : " + signature.getName() + "(" + Arrays.deepToString(args) + ")\n" + "activate " + aThis;
        }
    }

    private String getTarget(JoinPoint thisJoinPoint) {
        if(thisJoinPoint == null || thisJoinPoint.getTarget() == null) return null;
        return thisJoinPoint.getTarget().getClass().getSimpleName();
    }

    private String getThis(JoinPoint thisJoinPoint) {
        if(thisJoinPoint != null && thisJoinPoint.getThis() != null) return thisJoinPoint.getThis().getClass().getSimpleName();
        String name = thisJoinPoint.getStaticPart().getSignature().getDeclaringTypeName();
        return name.substring((name.lastIndexOf(".")+1));
    }
}
