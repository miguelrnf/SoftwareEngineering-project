import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/LoginView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import StudentView from '@/views/student/StudentView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import AvailableQuizzesView from '@/views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from '@/views/student/SolvedQuizzesView.vue';
import QuizView from '@/views/student/quiz/QuizView.vue';
import ResultsView from '@/views/student/quiz/ResultsView.vue';
import ScanView from '@/views/student/ScanView.vue';
import AvailableTournamentsView from '@/views/student/tournament/AvailableTournamentsView.vue';
import OwnTournamentsView from '@/views/student/tournament/OwnTournamentsView.vue';
import TournamentsView from '@/views/admin/TournamentsView.vue';
import CreateTournamentsView from '@/views/student/tournament/CreateTournamentsView.vue';
import PostsView from '@/views/PostsView.vue';
import PostPostView from '@/views/student/PostPostView.vue';
import DashboardHomeView from '@/views/DashboardHomeView.vue';
import AdminManagementView from './views/admin/AdminManagementView.vue';
import NotFoundView from './views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';
import CoursesView from '@/views/admin/Courses/CoursesView.vue';
import SuggestionsView from '@/views/suggestions/SuggestionsView.vue';
import EnrolledTournamentsView from '@/views/student/tournament/EnrolledTournamentsView.vue';
import PostGeneralView from '@/views/PostGeneralView.vue';
import DashboardGeneralView from '@/views/DashboardGeneralView.vue';
import AllTeacherTournaments from '@/views/teacher/AllTeacherTournaments.vue';
import ShopGeneralView from '@/views/ShopGeneralView.vue';
import ShopHomeView from '@/views/ShopCategoryView.vue';
import StudyGeneralView from '@/views/StudyGeneralView.vue';
import StudyHomeView from '@/views/student/study/StudyHomeView.vue';
import ThemeInventory from '@/views/ThemeInventory.vue';
import CreateShopItem from '@/views/admin/CreateShopItem.vue';

Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: process.env.VUE_APP_NAME, requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Login',
        requiredAuth: 'None'
      }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'suggestions',
          name: 'suggestions-management',
          component: SuggestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - suggestions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'tournaments',
          name: 'all-tournaments',
          component: AllTeacherTournaments,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Tournaments',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'createTournaments',
          name: 'create-teacher-tournament',
          component: CreateTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Tournaments',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Teacher',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - ImpExp',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'availableTournaments',
          name: 'available-Tournaments',
          component: AvailableTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'enrolledTournaments',
          name: 'enrolled-Tournaments',
          component: EnrolledTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Enrolled Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'ownTournaments',
          name: 'own-Tournaments',
          component: OwnTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Own Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'createTournaments',
          name: 'create-tournament',
          component: CreateTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quiz',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'suggestions',
          name: 'suggestions',
          component: SuggestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - suggestions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Scan',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/posts',
      name: 'posts',
      component: PostGeneralView,
      children: [
        {
          path: 'submit',
          name: 'submit-post',
          component: PostPostView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Submit Post',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'home',
          name: 'all-posts',
          component: PostsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - All Posts',
            requiredAuth: 'None'
          }
        }
      ]
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardGeneralView,
      children: [
        {
          path: 'home',
          name: 'dashboard-home',
          component: DashboardHomeView,
          props: { isOwnDashboard: true, isReal: true },
          meta: {
            title: process.env.VUE_APP_NAME + ' - Dashboard',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/shop',
      name: 'shop',
      component: ShopGeneralView,
      children: [
        {
          path: 'home',
          name: 'shop-home',
          component: ShopHomeView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Shop',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/study',
      name: 'study',
      component: StudyGeneralView,
      children: [
        {
          path: 'home',
          name: 'study-home',
          component: StudyHomeView,
          props: { isOwnDashboard: true, isReal: true },
          meta: {
            title: process.env.VUE_APP_NAME + ' - Study',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Courses',
            requiredAuth: 'Admin'
          }
        },
        {
          path: 'tournaments',
          name: 'tournamentsAdmin',
          component: TournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Tournaments',
            requiredAuth: 'Admin'
          }
        }
      ]
    },
    {
      path: '/themes',
      name: 'themes',
      component: ThemeInventory,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Themes',
        requiredAuth: 'Student'
      }
    },
    {
      path: '/shopConfig',
      name: 'shop-config',
      component: CreateShopItem,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Create Item',
        requiredAuth: 'Admin'
      }
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
