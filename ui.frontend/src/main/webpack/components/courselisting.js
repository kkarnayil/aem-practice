(function () {
  "use strict";

  const STORAGE_KEY = "visitedCourses";

  let store = {
    courses: {}
  };

  function loadStore() {
    try {
      store = JSON.parse(localStorage.getItem(STORAGE_KEY)) || { courses: {} };
    } catch (e) {
      store = { courses: {} };
    }
  }

  function persistStore() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(store));
  }

  function getCourse(courseId) {
    if (!courseId) {
      return null;
    }

    if (!store.courses[courseId]) {
      store.courses[courseId] = {
        visited: false,
        lessonCount: 0,
        lessons: {}
      };
    }
    return store.courses[courseId];
  }


  function registerCoursesFromDOM() {
    document.querySelectorAll(".course-card").forEach(card => {
      const courseId = card.dataset.courseId;
      const lessonCount = parseInt(card.dataset.lessonsCount, 10);

      if (!courseId || !Number.isInteger(lessonCount) || lessonCount <= 0) {
        return;
      }

      const course = getCourse(courseId);
      if (!course.lessonCount || lessonCount > course.lessonCount) {
        course.lessonCount = lessonCount;
        updateCourseVisitedState(courseId);
      }
    });

    persistStore();
  }

  function bindLessonClicks() {
    document.addEventListener("click", function (e) {
      const lessonCard = e.target.closest(".lesson-card");
      if (!lessonCard) {
        return;
      }

      const courseId = lessonCard.dataset.courseId;
      const lessonId = lessonCard.dataset.lessonId;

      if (!courseId || !lessonId) {
        return;
      }

      markLessonVisited(courseId, lessonId);
      lessonCard.classList.add("is-visited");
    });
  }

  function markLessonVisited(courseId, lessonId) {
    const course = getCourse(courseId);

    if (!course.lessons[lessonId]) {
      course.lessons[lessonId] = { visited: true };
    } else {
      course.lessons[lessonId].visited = true;
    }

    updateCourseVisitedState(courseId);
    persistStore();
  }


  function updateCourseVisitedState(courseId) {
    const course = store.courses[courseId];
    if (!course || !course.lessonCount) {
      course.visited = false;
      return;
    }

    const visitedLessons = Object.values(course.lessons)
      .filter(l => l.visited === true).length;

    course.visited = visitedLessons === course.lessonCount;
  }


  function applyVisitedState() {
    // Course cards
    document.querySelectorAll(".course-card").forEach(card => {
      const courseId = card.dataset.courseId;
      if (store.courses[courseId]?.visited) {
        card.classList.add("is-visited");
      }
    });

    // Lesson cards
    document.querySelectorAll(".lesson-card").forEach(card => {
      const courseId = card.dataset.courseId;
      const lessonId = card.dataset.lessonId;

      if (store.courses[courseId]?.lessons?.[lessonId]?.visited) {
        card.classList.add("is-visited");
      }
    });
  }

  /* =========================
     INIT
  ==========================*/

  function init() {
    loadStore();
    registerCoursesFromDOM();
    bindLessonClicks();
    applyVisitedState();
  }

  document.addEventListener("DOMContentLoaded", init);
})();
