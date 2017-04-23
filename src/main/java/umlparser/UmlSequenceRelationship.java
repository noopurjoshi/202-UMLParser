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
    
    // methods in classes
    @Before("within(*.*) && call(* *.*.*(..)) && !within(UmlSequenceRelationship)")
    public void beforeMethodCall(JoinPoint thisJoinPoint) {
        traceEntry(getThis(thisJoinPoint), getTarget(thisJoinPoint), thisJoinPoint.getSignature(), thisJoinPoint.getSourceLocation(), thisJoinPoint.getArgs());
    }

    private void traceEntry(final String aThis, final String target, final Signature signature, final SourceLocation sourceLocation, final Object[] args) {
        if(aThis != null && target != null) {
            String message = aThis + " -> " + target + " : " + signature.getName() + "(" + Arrays.deepToString(args) + ")\n" + "activate " + aThis;
        }
    }
	
	@AfterReturning("within(*.*) && call(* *.*.*(..)) && !within(TracingAspect)")
    public void before4(JoinPoint thisJoinPoint) {
        traceExit(getThis(thisJoinPoint), getTarget(thisJoinPoint), thisJoinPoint.getSignature(), thisJoinPoint.getSourceLocation(), thisJoinPoint.getArgs());
    }
	
	private void traceExit(final String aThis, String target, final Signature signature, final SourceLocation sourceLocation, final Object... returnValue) {
        if(aThis != null && target != null) {
            String message = target + " -> " + aThis + " : return" + "(" + Arrays.deepToString(returnValue) + ")\n deactivate " + aThis;
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
