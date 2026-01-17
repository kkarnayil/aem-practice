(function () {
  "use strict";

  const STORAGE_KEY = "visitedCourses";
  let visitedCourses = [];
  let visitedCourseSet = new Set();

  function loadVisitedCourses() {
    try {
      visitedCourses = JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
    } catch (e) {
      visitedCourses = [];
    }

    visitedCourseSet = new Set(
      visitedCourses
        .filter(c => c.visited === true)
        .map(c => c.courseId)
    );
  }

  function persistVisitedCourses() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(visitedCourses));
  }

  function markCourseVisited(courseId) {
    if (!courseId || visitedCourseSet.has(courseId)) {
      return;
    }

    visitedCourseSet.add(courseId);
    visitedCourses.push({
      courseId: courseId,
      visited: true
    });

    persistVisitedCourses();
  }

  function applyVisitedState() {
    const container = document.querySelector(".course-listing");
    if (!container) {
      return;
    }

    container.querySelectorAll(".course-card").forEach(card => {
      if (visitedCourseSet.has(card.dataset.courseId)) {
        card.classList.add("is-visited");
      }
    });
  }

  function checkAndMarkCourseVisited() {
    const component = document.querySelector(".course-selector");
    if (!component) {
      return;
    }

    const selectors = (component.dataset.selectors || "").split(",");
    const courseId = selectors[0];

    if (courseId) {
      markCourseVisited(courseId);
    }
  }

  function init() {
    loadVisitedCourses();
    checkAndMarkCourseVisited();
    applyVisitedState();
  }

  document.addEventListener("DOMContentLoaded", init);
})();
