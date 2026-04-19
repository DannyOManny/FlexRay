package com.pantheon.PantheonEngine.moulder;

public class FlexRay {
    //Capacity Logic Caps
    private static final int[] CAPACITY = {1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288};
    private int capIndex = 0; //Allocation in the Capacity

    private float[][][] dsSetFloat; //Main Data Structure/Pointers

    private final int CHANNELS; //Active Columns
    private int SETCOUNT = 0; //Active set (0-63)
    private int DATACOUNT = 0; //Next available slot in the given array

    private int dataCount = 0; //Amount of Data currently in the Structure

    private int[] getDataSize = new int[65]; //Consider making it final, 1 more loop counter
    
    public FlexRay(int dataSize){
        //Set Data size (Max 64 columns)(0-63)
        CHANNELS = ((dataSize & ~(dataSize>>31)) < 64 ? (dataSize & ~(dataSize>>31)) : 63);

        //Initialize Data Structures 0-63
        dsSetFloat = new float[CHANNELS][][];

        final int capIndexInit = CAPACITY[capIndex];

        for(int i = 0; i < CHANNELS ; i++) {
            dsSetFloat[i] = new float[64][];
            dsSetFloat[i][0] = new float[capIndexInit];
        }

        getDataAdjustSizes();
    }


    public void addData(float[] val){
        //Create a local Reference

        final int tempLength = val.length;
        final int tempChan = CHANNELS;
        final int tempSet = SETCOUNT;
        final int tempLoc = DATACOUNT;

        for(int i = 0 ; i < tempLength && i < tempChan ; i++){
            dsSetFloat[i][tempSet][tempLoc] = val[i];
        }

        System.out.println("Added : "+tempSet+" | "+tempLoc + " | Count: "+dataCount);
        dataCount++;
        adjustSet();
    }

    private void adjustSet() {
        final int setCount = SETCOUNT;
        if (DATACOUNT < dsSetFloat[0][setCount].length - 1) {
            DATACOUNT++;
            return;
        }

        final int nextSet = (setCount + 1) & 63;

        int newCapacity = CAPACITY[capIndex];
        if (nextSet == 0) { capIndex++; newCapacity = CAPACITY[capIndex]; } //GIves error if MAX capacity is reached

        final float[] target = dsSetFloat[0][nextSet];
        final int chan = CHANNELS; 

        if (target == null) {
            for (int i = 0; i < chan; i++) {
                dsSetFloat[i][nextSet] = new float[newCapacity];
            }
            DATACOUNT = 0;

        } else {
            final int oldSize = dsSetFloat[0][nextSet].length;
            for (int i = 0; i < chan; i++) {
                final float[] oldArray = dsSetFloat[i][nextSet];
                final float[] newArray = new float[newCapacity];  
                System.arraycopy(oldArray, 0, newArray, 0, oldSize);
                dsSetFloat[i][nextSet] = newArray; 
            }
            DATACOUNT = oldSize;
        }
        SETCOUNT = nextSet;
        getDataAdjustSizes();
    }

    public float[] getData(int refID){

        final int columns = CHANNELS;
        final float[] dataValues = new float[columns];
        final int[] dataSizes = getDataSize;
        
        final int S_dataCount = dataCount;

        System.out.println("Retrieved : Count: "+dataCount);
        if(refID>S_dataCount || refID < 0 || refID>=S_dataCount)return dataValues;

        int alocSET = 0;
        int alocDATA = 0;

        for(int i = 1 ; i < 64 ; i++){
            if(refID<=dataSizes[i]){
                alocSET = i-1;
                alocDATA = refID-dataSizes[alocSET];
                break;
            }
        }
        for(int i = 0 ; i < columns ; i++){
            dataValues[i] = dsSetFloat[i][alocSET][alocDATA];
        }
        System.out.println("Retrieved : "+alocSET+" | "+alocDATA + " | Count: "+dataCount);
        return dataValues;
    }

    private void getDataAdjustSizes(){
        final float[][] setRef = dsSetFloat[0];
        final int[] setSizes = getDataSize;

        int accu = 0;
        setSizes[0] = accu;

        int loopCount = 0;
        for(int i = 1 ; i < 64 && setRef[i] != null ; i++){
            accu += setRef[i].length;
            setSizes[i] = accu;
            loopCount = i;
        }

        setSizes[loopCount+1] = dataCount;

        getDataSize = setSizes;
        //for(int i = 0 ; i<64 ; i++){ System.out.println("Size : "+i + " | " +getDataSize[i]); } //TESTER
    }


}
