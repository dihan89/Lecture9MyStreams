import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;



public class StreamMy<T> {
    private static final String filterMethod = "filterExecute";
    private static final String transformMethod = "transformExecute";
    private static final String forEachMethod = "forEachExecute";
    private static final String toMapMethod = "toMapExecute";

    private Spliterator<T> iter;
    private List<T> list;
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
            sequenceMethods.add(new Pair<>
                    (thisClass.getDeclaredMethod(forEachMethod, Consumer.class),
                            new Object[]{cons}));
            execute();
        } catch(NoSuchMethodException exc){
            System.out.println("EXCEPTION!"+exc);
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
    }

    public <K> StreamMy<K> transform(Function<? super T, ? extends K> func) {
        try {
             sequenceMethods.add( new Pair<>
                        (thisClass.getDeclaredMethod(transformMethod, Function.class),
                                new Object[]{func}));
            return (StreamMy<K>) execute();
        } catch(NoSuchMethodException exc){
            System.out.println("EXCEPTION!"+exc);
            return null;
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
    }


    public <K, V> Map<K,V> toMap (Function<? super T, ? extends K> keyMapper,
                                          Function<? super T, ? extends V> valueMapper) {
        try {
            sequenceMethods.add(new Pair<>
                    (thisClass.getDeclaredMethod(toMapMethod, Function.class, Function.class),
                            new Object[]{keyMapper, valueMapper}));
            return (Map<K, V>) execute();
        }  catch(NoSuchMethodException exc){
        System.out.println("EXCEPTION!"+exc);
        return null;
        } catch(NullPointerException exc){
            throw new IllegalStateException("Stream has been already used");
        }
    }

    private StreamMy (List<T> list){
        this.list = list;
        sequenceMethods = new ArrayList<>();
    }

    private Object execute() throws IllegalStateException {
        try {
            iter = list.spliterator();
            for (int i = 0; i < sequenceMethods.size() - 1; ++i) {
                Pair<Method, Object[]> methPair = sequenceMethods.get(i);
                methPair.method.invoke(this, methPair.args);
            }
            Pair<Method, Object[]> methPair = sequenceMethods.get(sequenceMethods.size() - 1);
            sequenceMethods = null;
            return methPair.method.invoke(this, methPair.args);
        } catch (IllegalStateException exc){
            System.out.println("EXCEPTION! " + exc);
            return null;
        } catch (NullPointerException exc){
            System.out.println("Stream has been already used!");
            return null;
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
        return null;
    }

    private StreamMy<T> filterExecute(Predicate<? super T> predicate) {
        List<T> listReturn = new ArrayList<>();
        iter.forEachRemaining((T t) ->{
            if (predicate.test(t))
                listReturn.add(t);
        } );
        this.list = listReturn;
        this.iter = listReturn.spliterator();
        return this;
    }

    private <K> StreamMy<K> transformExecute(Function<? super T, ? extends K> func) {
        ArrayList<K> transformedStreamList = new ArrayList<>();
        iter.forEachRemaining((T t) -> transformedStreamList.add(func.apply(t)));
        return StreamMy.of(transformedStreamList);
    }

    private void forEachExecute(Consumer<? super T> cons) {
        iter.forEachRemaining(cons);
    }


    private <K, V> Map<K,V> toMapExecute (Function<? super T, ? extends K> keyMapper,
                                  Function<? super T, ? extends V> valueMapper) {
        Map<K,V> returnMap = new HashMap<>();
        iter.forEachRemaining((T t)-> {
            var key = keyMapper.apply(t);
            if (returnMap.containsKey(key))
                throw new IllegalStateException("Duplicate key: " + key);
            returnMap.put(key, valueMapper.apply(t));
        });
        return returnMap;
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
