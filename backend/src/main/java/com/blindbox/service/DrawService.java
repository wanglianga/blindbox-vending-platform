package com.blindbox.service;

import com.blindbox.vo.DrawResultVO;
import com.blindbox.vo.MachineDetailVO;

public interface DrawService {

    MachineDetailVO getMachineDetail(String machineCode);

    DrawResultVO draw(String machineCode, String payType);
}
