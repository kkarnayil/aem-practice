export default class LessonInteraction {

  static LESSON_SELECTOR = '.lesson-card';

  constructor(store, root) {
    this.store = store;
    this.root = root;
  }

  init() {
    this.registerCourseFromLessons();
    this.bindLessonClicks();
    this.applyVisitedLessons();
  }

  registerCourseFromLessons() {

    const lessonsByCourse = new Map();
    this.root.querySelectorAll(LessonInteraction.LESSON_SELECTOR)
      .forEach(card => {
        const courseId = card.dataset.courseId;
        if (!courseId) return;

        lessonsByCourse.set(
          courseId,
          (lessonsByCourse.get(courseId) || 0) + 1
        );
      });

    lessonsByCourse.forEach((count, courseId) => {
      this.store.setLessonCount(courseId, count);
      this.store.updateCourseVisited(courseId);
    });

    this.store.save();
  }

  bindLessonClicks() {
    this.root.querySelectorAll(LessonInteraction.LESSON_SELECTOR).forEach(card => {
      card.addEventListener("click", () => {
        const { courseId, lessonId } = card.dataset;
        if (!courseId || !lessonId) return;

        this.store.markLessonVisited(courseId, lessonId);
        card.classList.add("is-visited");
      });
    });
  }

  applyVisitedLessons() {
    this.root.querySelectorAll(LessonInteraction.LESSON_SELECTOR).forEach(card => {
      const { courseId, lessonId } = card.dataset;
      if (this.store.data.courses[courseId]?.lessons?.[lessonId]?.visited) {
        card.classList.add("is-visited");
      }
    });
  }
}
