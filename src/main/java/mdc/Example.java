package mdc;

public class Example {
    public static void main(String[] args) throws InterruptedException {

        Thread thread1 = new Thread(() -> {
            UploadController.uploadFile(null);
        });

        Thread thread2 = new Thread(() -> {
            UploadController.uploadFile(null);
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}


