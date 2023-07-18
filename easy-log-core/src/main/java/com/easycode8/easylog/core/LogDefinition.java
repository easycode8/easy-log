package com.easycode8.easylog.core;

import java.io.Serializable;

public interface LogDefinition extends Serializable {
    Integer STATUS_UN_INIT = 0;
    Integer STATUS_INIT = 1;
    Integer STATUS_BEFORE = 2;
    Integer STATUS_FINISH = 3;

}
