export default class CourseStore {
  constructor(key) {
    this.key = key;
    this.data = { courses: {} };
  }

  load() {
    try {
      this.data = JSON.parse(localStorage.getItem(this.key)) || { courses: {} };
    } catch {
      this.data = { courses: {} };
    }
  }

  save() {
    localStorage.setItem(this.key, JSON.stringify(this.data));
  }

  getCourse(courseId) {
    if (!courseId) return null;

    if (!this.data.courses[courseId]) {
      this.data.courses[courseId] = {
        lessonCount: 0,
        lessons: {},
        visited: false
      };
    }
    return this.data.courses[courseId];
  }

  setLessonCount(courseId, count) {
    const course = this.getCourse(courseId);
    if (!course) return;

    if (!course.lessonCount || count > course.lessonCount) {
      course.lessonCount = count;
    }
  }

  markLessonVisited(courseId, lessonId) {
    const course = this.getCourse(courseId);
    if (!course) return;

    course.lessons[lessonId] = { visited: true };
    this.updateCourseVisited(courseId);
    this.save();
  }

  updateCourseVisited(courseId) {
    const course = this.data.courses[courseId];
    if (!course || !course.lessonCount) return;

    const visitedCount = Object.values(course.lessons)
      .filter(l => l.visited).length;

    course.visited = visitedCount === course.lessonCount;
  }
}
