package io.d8a.conjure;

public interface Clock {
    public static final Clock SYSTEM_CLOCK = new Clock(){

        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        @Override
        public void sleep(long millis) {
            try{
                Thread.sleep(millis);
            }catch(InterruptedException ex){
            }
        }

        public String toString(){
            return "System Clock";
        }
    };
    long currentTimeMillis();
    void sleep(long millis);
}
