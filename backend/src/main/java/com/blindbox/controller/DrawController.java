package com.blindbox.controller;

import com.blindbox.common.Result;
import com.blindbox.dto.DrawDTO;
import com.blindbox.service.DrawService;
import com.blindbox.vo.DrawResultVO;
import com.blindbox.vo.MachineDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    @GetMapping("/machine/{machineCode}")
    public Result<MachineDetailVO> getMachineDetail(@PathVariable String machineCode) {
        return Result.success(drawService.getMachineDetail(machineCode));
    }

    @PostMapping("/do")
    public Result<DrawResultVO> draw(@Valid @RequestBody DrawDTO drawDTO) {
        return Result.success(drawService.draw(drawDTO.getMachineCode(), drawDTO.getPayType()));
    }
}
