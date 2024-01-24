public class Main2 {
    public static void main(String[] args) {

        for(int i=1; i<=3; i++){
            MultiThreadThing multiThreadThing = new MultiThreadThing(i);
            Thread myThread = new Thread(multiThreadThing);
            myThread.start();

//            USE .join() IF U WANT TO WAIT FOR THE CURRENT THREAD TO FINISH BEFORE CALLING THE NEXT THREAD
            try {
                myThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        throw new RuntimeException();

    }
}
