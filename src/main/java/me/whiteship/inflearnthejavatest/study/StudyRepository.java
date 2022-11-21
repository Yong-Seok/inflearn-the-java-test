package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Study;

public interface StudyRepository {
    Study save(Study study);
}
