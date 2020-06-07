<template>
  <nav>
    <v-app-bar color="primary" clipped-left>
      <v-app-bar-nav-icon
        @click.stop="drawer = !drawer"
        class="hidden-md-and-up"
        aria-label="Menu"
      />

      <v-toolbar-title>
        <v-btn
          dark
          active-class="no-active"
          text
          tile
          to="/"
          v-if="currentCourse"
        >
          {{ currentCourse.name }}
        </v-btn>
        <v-btn dark active-class="no-active" text tile to="/" v-else>
          {{ appName }}
        </v-btn>
        <v-switch @change="changeMode" label="Dark"></v-switch>
      </v-toolbar-title>

      <v-spacer />

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-menu offset-y v-if="isAdmin" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="administrationMenuButton">
              Administration
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/admin/courses" data-cy="manageCoursesMenuButton">
              <v-list-item-action>
                <v-icon>fas fa-school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Manage Courses</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/admin/tournaments">
              <v-list-item-action>
                <v-icon>fas fa-trophy</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Tournaments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isTeacher && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="management">
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/management/questions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/topics">
              <v-list-item-action>
                <v-icon>category</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Topics</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/quizzes">
              <v-list-item-action>
                <v-icon>ballot</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Quizzes</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/tournaments">
              <v-list-item-action>
                <v-icon>fas fa-trophy</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="tournaments">
                  Tournaments</v-list-item-title
                >
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/assessments">
              <v-list-item-action>
                <v-icon>book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Assessments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/students">
              <v-list-item-action>
                <v-icon>school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Students</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/impexp">
              <v-list-item-action>
                <v-icon>cloud</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>ImpExp</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/suggestions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Suggestions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/posts/home">
              <v-list-item-action>
                <v-icon>fas fa-book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Posts</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              Tournament
              <v-icon>fas fa-trophy</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item>
              <v-list-item-action>
                <v-icon>fas fa-coins</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title
                  >{{
                    $store.getters.getUser.score
                  }}
                  Achandos</v-list-item-title
                >
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/createTournaments">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="create">Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/availableTournaments">
              <v-list-item-action>
                <v-icon>fas fa-list-ul</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Created</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/enrolledTournaments">
              <v-list-item-action>
                <v-icon>fas fa-award</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Enrolled</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/ownTournaments">
              <v-list-item-action>
                <v-icon>fas fa-user</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Own</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="Student">
              Quizzes
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/available">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Available</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/create">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/scan">
              <v-list-item-action>
                <v-icon>fas fa-qrcode</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Scan</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/solved">
              <v-list-item-action>
                <v-icon>done</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Solved</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/posts/home">
              <v-list-item-action>
                <v-icon>fas fa-book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Posts</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/posts/submit">
              <v-list-item-action>
                <v-icon>fas fa-plus</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Submit Post</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/suggestions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Suggestions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn to="/student/stats" v-if="isStudent && currentCourse" text dark>
          Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn
          to="/dashboard/home"
          v-if="isStudent && currentCourse"
          text
          dark
          data-cy="Dashboard"
        >
          Dashboard
          <v-icon>fas fa-star</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn && moreThanOneCourse"
          to="/courses"
          active-class="no-active"
          text
          dark
        >
          Change course
          <v-icon>fa fa-book</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn"
          @click="logout"
          data-cy="logoutButton"
          text
          dark
        >
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-else :href="fenixUrl" text dark>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>

        <v-btn href="https://www.worldometers.info/coronavirus/" text dark>
          #STAYHOME
          <br />
          #STAYSAFE
          <v-icon>fas fa-virus</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer app v-model="drawer" absolute dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <!-- Administration Group-->
        <v-list-group
          prepend-icon="fas fa-file-alt"
          :value="false"
          v-if="isAdmin"
        >
          <template v-slot:activator>
            <v-list-item-title>Administration</v-list-item-title>
          </template>
          <v-list-item to="/admin/courses">
            <v-list-item-action>
              <v-icon>fas fa-school</v-icon>
            </v-list-item-action>
            <v-list-item-content>Manage Courses</v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/tournaments">
            <v-list-item-action>
              <v-icon>fas fa-trophy</v-icon>
            </v-list-item-action>
            <v-list-item-content>Tournaments</v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Management Group-->
        <v-list-group
          prepend-icon="fas fa-file-alt"
          :value="false"
          v-if="isTeacher && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-title data-cy="Management">
              Management</v-list-item-title
            >
          </template>
          <v-list-item to="/management/questions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Questions</v-list-item-title>
            </v-list-item-content> </v-list-item
          ><v-list-item to="/management/suggestions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Suggestions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/topics">
            <v-list-item-action>
              <v-icon>category</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Topics</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/quizzes">
            <v-list-item-action>
              <v-icon>ballot</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Quizzes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/tournaments">
            <v-list-item-action>
              <v-icon>fas fa-trophy</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Tournaments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/assessments">
            <v-list-item-action>
              <v-icon>book</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Assessments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/students">
            <v-list-item-action>
              <v-icon>school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Students</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/impexp">
            <v-list-item-action>
              <v-icon>cloud</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>ImpExp</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/posts/home">
            <v-list-item-action>
              <v-icon>fas fa-book</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Posts List</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Student Group-->
        <v-list-group
          prepend-icon="ballot"
          :value="false"
          v-if="isStudent && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-content class="mobileTitle"
              >Quizzes</v-list-item-content
            >
          </template>

          <v-list-item to="/student/available">
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Available Quizzes</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/create">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Create Quiz</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/scan">
            <v-list-item-action>
              <v-icon>fas fa-qrcode</v-icon>
            </v-list-item-action>
            <v-list-item-content>Scan</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/solved">
            <v-list-item-action>
              <v-icon>done</v-icon>
            </v-list-item-action>
            <v-list-item-content>Solved Quizzes</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/suggestions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>Suggestions</v-list-item-content>
          </v-list-item>

          <v-list-item to="/posts/home">
            <v-list-item-action>
              <v-icon>fas fa-book</v-icon>
            </v-list-item-action>
            <v-list-item-content>Posts List</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/stats">
            <v-list-item-action>
              <v-icon>fas fa-user</v-icon>
            </v-list-item-action>
            <v-list-item-content>Stats</v-list-item-content>
          </v-list-item>

          <v-list-item to="/posts/submit">
            <v-list-item-action>
              <v-icon>fas fa-plus</v-icon>
            </v-list-item-action>
            <v-list-item-content>Submit Post</v-list-item-content>
          </v-list-item>
        </v-list-group>

        <v-list-group
          prepend-icon="fas fa-trophy"
          :value="false"
          v-if="isStudent && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-content class="mobileTitle"
              >Tournaments</v-list-item-content
            >
          </template>

          <v-list-item to="/student/availableTournaments">
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Available</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/createTournaments">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Create Tournaments</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/enrolledTournaments">
            <v-list-item-action>
              <v-icon>fas fa-award</v-icon>
            </v-list-item-action>
            <v-list-item-content>Enrolled</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/ownTournaments">
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Own</v-list-item-content>
          </v-list-item>
        </v-list-group>

        <v-list-item to="/dashboard/home" v-if="isStudent && currentCourse">
          <v-icon>
            fas fa-star
          </v-icon>
        </v-list-item>

        <v-list-item to="/courses" v-if="isLoggedIn && moreThanOneCourse">
          <v-list-item-action>
            <v-icon>fas fa-book</v-icon>
          </v-list-item-action>
          <v-list-item-content>Change course</v-list-item-content>
        </v-list-item>
        <v-list-item @click="logout" v-if="isLoggedIn">
          <v-list-item-action>
            <v-icon>fas fa-sign-out-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content class="mobileTitle">Logout</v-list-item-content>
        </v-list-item>
        <v-list-item :href="fenixUrl" v-else>
          <v-list-item-action>
            <v-icon>fas fa-sign-in-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content class="mobileTitle">Login</v-list-item-content>
        </v-list-item>
        <v-list-item href="https://www.worldometers.info/coronavirus/">
          <v-list-item-action>
            <v-icon>fas fa-virus</v-icon>
          </v-list-item-action>
          <v-list-item-content class="mobileTitle">
            #STAYHOME
            <br />
            #STAYSAFE
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <!-- End of mobile side menu -->
  </nav>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class TopBar extends Vue {
  fenixUrl: string = process.env.VUE_APP_FENIX_URL;
  appName: string = process.env.VUE_APP_NAME;
  drawer: boolean = false;

  get currentCourse() {
    return this.$store.getters.getCurrentCourse;
  }

  get moreThanOneCourse() {
    return (
      this.$store.getters.getUser.coursesNumber > 1 &&
      this.$store.getters.getCurrentCourse
    );
  }

  get isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  get isTeacher() {
    return this.$store.getters.isTeacher;
  }

  get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  get isStudent() {
    return this.$store.getters.isStudent;
  }

  async logout() {
    await this.$store.dispatch('logout');
    await this.$router.push({ name: 'home' }).catch(() => {});
  }

  changeMode() {
    if (!this.$vuetify.theme.dark) {
      this.$vuetify.theme.dark = true;
      this.$vuetify.theme.themes.light.primary = '#25302b';
      this.$vuetify.theme.themes.dark.primary = '#25302b';

      this.$vuetify.theme.themes.dark.accent = '#829ab1';
      this.$vuetify.theme.themes.light.accent = '#829ab1';

      this.$vuetify.theme.themes.dark.secondary = '#393e46';
      this.$vuetify.theme.themes.light.secondary = '#393e46';

      this.$vuetify.theme.themes.dark.info = '#4ecca3';
      this.$vuetify.theme.themes.light.info = '#4ecca3';

      this.$vuetify.theme.themes.dark.warning = '#102a43';
      this.$vuetify.theme.themes.light.warning = '#102a43';

      this.$vuetify.theme.themes.dark.error = '#ec625f';
      this.$vuetify.theme.themes.light.error = '#ec625f';

      this.$vuetify.theme.themes.light.success = '#cee397';
      this.$vuetify.theme.themes.dark.success = '#cee397';
    } else {
      this.$vuetify.theme.dark = false;
      this.$vuetify.theme.themes.light.primary = '#1976D2';
      this.$vuetify.theme.themes.dark.primary = '#1976D2';

      this.$vuetify.theme.themes.light.accent = '#828282';
      this.$vuetify.theme.themes.dark.accent = '#828282';

      this.$vuetify.theme.themes.light.secondary = '#d8d8d8';
      this.$vuetify.theme.themes.light.info = '#2196F3';
      this.$vuetify.theme.themes.light.warning = '#FB8C00';
      this.$vuetify.theme.themes.light.error = '#FF5252';
      this.$vuetify.theme.themes.light.success = '#4CAF50';

      this.$vuetify.theme.themes.dark.secondary = '#d8d8d8';
      this.$vuetify.theme.themes.dark.info = '#2196F3';
      this.$vuetify.theme.themes.dark.warning = '#FB8C00';
      this.$vuetify.theme.themes.dark.error = '#FF5252';
      this.$vuetify.theme.themes.dark.success = '#4CAF50';
    }
  }
}
</script>

<style lang="scss" scoped>
.no-active::before {
  opacity: 0 !important;
}
.mobileTitle {
  font-weight: bold;
  font-size: large;
}
nav {
  z-index: 300;
}
</style>
