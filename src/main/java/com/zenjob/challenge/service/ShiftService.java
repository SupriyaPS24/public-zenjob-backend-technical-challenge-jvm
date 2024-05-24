package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;

    private final JobRepository jobRepository;
    public List<Shift> getShifts(UUID id) {
        return shiftRepository.findAllByJobId(id);
    }

    public void bookTalent(UUID shiftId, UUID talent) {
        shiftRepository.findById(shiftId).ifPresent(shift -> {
            shift.setTalentId(talent);
            shift.setUpdatedAt(Instant.now());
            shiftRepository.save(shift);
        });
    }

    public boolean cancelShift(UUID shiftId) {
        Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);
        if (shiftOpt.isPresent()) {
            Shift shift = shiftOpt.get();
            Job job = shift.getJob();

            shiftRepository.delete(shift);

            long remainingShifts = shiftRepository.countByJobId(job.getId());
            if (remainingShifts == 0) {
                jobRepository.delete(job);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean cancelTalentAndReplaceShifts(UUID talentId) {
        List<Shift> shifts = shiftRepository.findByTalentId(talentId);

        if (shifts.isEmpty()) {
            return false; // No shifts found for the talent, operation fails
        }
        List<Shift> replacementShifts = new ArrayList<>();
        for (Shift shift : shifts) {
            Shift replacementShift = new Shift();
            replacementShift.setStartTime(shift.getStartTime());
            replacementShift.setEndTime(shift.getEndTime());
            replacementShift.setUpdatedAt(Instant.now());
            replacementShift.setCreatedAt(shift.getCreatedAt());
            replacementShift.setJob(shift.getJob());
            replacementShift.setId(shift.getId());
            replacementShifts.add(replacementShift);
        }
        try {
            shiftRepository.deleteAll(shifts);
            shiftRepository.saveAll(replacementShifts);
            return true;
        } catch (Exception ex) {
            return false; // Operation failed
        }
    }
}
