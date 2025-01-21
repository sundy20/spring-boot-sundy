package com.sundy.boot.pid;


public abstract class BasePID {

    protected PIDParam pidParam;

    public BasePID(PIDParam pidParam) {
        this.pidParam = pidParam;
    }

    abstract public double collectAndCalculate(double currentVal);

    final protected double adjustOutput(double output) {
        if (pidParam.isUseRange()) {
            if (output > pidParam.getRangeMax()) {
                return pidParam.getRangeMax();
            } else if (output < pidParam.getRangeMin()) {
                return pidParam.getRangeMin();
            }
        }
        return output;
    }
}
