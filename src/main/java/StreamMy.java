import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamMy<T> {
    private static final String filterMethod = "filterExecute";
    private static final String transformMethod = "transformExecute";
    private List<T> list;
    private StreamMy<?> startStream = null;
    private StreamMy<?> childStream = null;
    private ArrayList <Pair<Method, Object[]> > sequenceMethods;
    private final Class<?> thisClass = this.getClass();

    public static <T> StreamMy<T> of(List<T> list) {
        return new StreamMy<>(list);
    }

    public StreamMy<T> filter(Predicate<? super T> predicate){
        try {
            sequenceMethods.add( new Pair<>
                    (thisClass.getDeclaredMethod(filterMethod, Predicate.class),
                            new Object[]{predicate}));

        } catch(NoSuchMethodException exc){
            System.out.println(exc.getMessage());
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
        return this;
    }

    public void forEach(Consumer<? super T> cons) {
        try {
            startStream.execute();
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
        Spliterator<T> iter = list.spliterator();
        iter.forEachRemaining(cons);
    }

    public <K> StreamMy<K> transform(Function<? super T, ? extends K> func) {
        try {
            sequenceMethods.add( new Pair<>
                    (thisClass.getDeclaredMethod(transformMethod, Function.class),
                            new Object[]{func}));
        } catch(NoSuchMethodException exc){
            System.out.println("EXCEPTION!"+exc);
            return null;
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
        childStream = new StreamMy<K>(startStream);
        return (StreamMy<K>)childStream;
    }


    public <K, V> Map<K,V> toMap (Function<? super T, ? extends K> keyMapper,
                                  Function<? super T, ? extends V> valueMapper) {
        try {
            startStream.execute();
        }   catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
        Map<K,V> returnMap = new HashMap<>();
        Spliterator<T> iter = list.spliterator();
        iter.forEachRemaining((T t)-> {
            var key = keyMapper.apply(t);
            if (returnMap.containsKey(key))
                throw new IllegalStateException("Duplicate key: " + key);
            returnMap.put(key, valueMapper.apply(t));
        });
        return returnMap;
    }

    private StreamMy (List<T> list){
        this.list = list;
        sequenceMethods = new ArrayList<>();
        startStream = this;
    }

    private StreamMy (StreamMy<?> startStream){
        this.startStream = startStream;
        sequenceMethods = new ArrayList<>();
    }

    private void setList(List<?> list){
        this.list = (List<T>) list;
    }

    private void execute() throws IllegalStateException {
        try {;
            for (int i = 0; i < sequenceMethods.size(); ++i) {
                Pair<Method, Object[]> methPair = sequenceMethods.get(i);
                methPair.method.invoke(this, methPair.args);
            }
            if (childStream!= null) {
                childStream.execute();
            }
        } catch (IllegalStateException exc){
            System.out.println("EXCEPTION! " + exc);

        } catch (NullPointerException exc){
            System.out.println("Stream has been already used!");
        }
        catch (InvocationTargetException exc){
            System.out.println("EXCEPTION! " + exc.getCause());
            if (exc.getCause().getClass() == IllegalStateException.class)
                throw new IllegalStateException();
        }
        catch (IllegalAccessException exc){
            System.out.println("EXCEPTION! " + exc.getCause());
            throw new IllegalStateException();
        }
    }
    private StreamMy<T> filterExecute(Predicate<? super T> predicate) {
        List<T> listReturn = new ArrayList<>();
        Spliterator<T> iter = list.spliterator();
        iter.forEachRemaining((T t) ->{
            if (predicate.test(t))
                listReturn.add(t);
        } );
        this.list = listReturn;
        return this;
    }

    private <K> void transformExecute(Function<? super T, ? extends K> func) {
        ArrayList<K> transformedStreamList = new ArrayList<>();
        Spliterator<T> iter = list.spliterator();
        iter.forEachRemaining((T t) -> transformedStreamList.add(func.apply(t)));
        if(childStream != null)
            childStream.setList(transformedStreamList);
    }

    static private class Pair<A,B>{
        A method;
        B args;
        Pair(A method, B args){
            this.method = method;
            this.args = args;

        }
    }
}


