package com.blindbox.controller.mall;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.common.UserContext;
import com.blindbox.service.MachinePointService;
import com.blindbox.vo.MachinePointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mall/machine")
@RequiredArgsConstructor
public class MallMachineController {

    private final MachinePointService machinePointService;

    @GetMapping("/page")
    public Result<IPage<MachinePointVO>> getMachinePage(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long mallId = UserContext.getCompanyId();
        return Result.success(machinePointService.getMachinePage(mallId, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MachinePointVO> getMachineDetail(@PathVariable Long id) {
        return Result.success(machinePointService.getMachineDetail(id));
    }
}
