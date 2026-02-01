import CourseStore from "./CourseStore";
import CourseTracker from "./CourseTracker";
import LessonInteraction from "./LessonInteraction";

document.addEventListener("DOMContentLoaded", () => {
  const course = document.querySelector(".course-card");
  const lessonRoot = document.querySelector(".lesson-listing");
  const COURSE_STORAGE_KEY = "course-tracker-data";

  // If neither component exists, do nothing
  if (!course && !lessonRoot) {
    return;
  }

  const store = new CourseStore(COURSE_STORAGE_KEY);
  store.load();

  if (course) {
    new CourseTracker(store, document).init();
  }

  if (lessonRoot) {
    new LessonInteraction(store, lessonRoot).init();
  }
});