export default class CourseTracker {

  static COURSE_CARD = ".course-card";

  constructor(store, root) {
    this.store = store;
    this.root = root;
  }

  init() {
    this.registerCourses();
    this.applyCourseState();
  }

  registerCourses() {
    this.root.querySelectorAll(CourseTracker.COURSE_CARD).forEach(card => {
      const courseId = card.dataset.courseId;
      const lessonCount = Number(card.dataset.lessonsCount);

      if (!courseId || !Number.isInteger(lessonCount)) {
        return;
      }

      this.store.setLessonCount(courseId, lessonCount);
      this.store.updateCourseVisited(courseId);
    });

    this.store.save();
  }

  applyCourseState() {
    this.root.querySelectorAll(CourseTracker.COURSE_CARD).forEach(card => {
      const courseId = card.dataset.courseId;
      if (this.store.data.courses[courseId]?.visited) {
        card.classList.add("is-visited"); // greyed out
      }
    });
  }
}
