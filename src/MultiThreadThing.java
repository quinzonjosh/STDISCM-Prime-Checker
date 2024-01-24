public class MultiThreadThing implements Runnable {

    private final int threadNumber;

    public MultiThreadThing(int thread){
        this.threadNumber = thread;
    }

    @Override
    public void run(){
        for(int i=1; i<=5; i++){
            System.out.println("Task " + i + " from thread " + threadNumber);
//            if (threadNumber == 3){
//                throw new RuntimeException();
//            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
