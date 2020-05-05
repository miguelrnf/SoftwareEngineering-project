<template>
  <div align="center">
    <v-card width="95%" v-if="isReal">
      <v-row>
        <v-card-subtitle class="text-center">
          Dashboard ??
        </v-card-subtitle>
        <v-spacer />
        <v-autocomplete
          v-if="isOwnDashboard"
          v-model="searchedStudent"
          :items="students"
          item-text="name"
          append-icon="search"
          label="Search for dashboards"
          class="mx-6 mt-3"
          @keydown.enter.native="searchForDashboard(searchedStudent)"
        />
      </v-row>
      <v-card>
        <v-card-subtitle class="text-left">User details ?</v-card-subtitle>
        <v-card-text class="text-left">Some details here ?</v-card-text>
      </v-card>
      <v-row scrollable no-gutters>
        <v-col v-for="n in 3" :key="n" cols="12" sm="4">
          <v-card tile outlined>
            <v-app-bar dense elevation="2" color="grey lighten-2" flat>
              <v-toolbar-title>{{ getColumnAppBar(n) }}</v-toolbar-title>
            </v-app-bar>
            <v-card style="max-height: 550px" class="overflow-y-auto" flat>
              <div v-if="n === 1 && isOwnDashboard">
                <post-preview
                  v-for="p in posts"
                  :key="p.id"
                  :post="p"
                  @click.native="showPostOpenDialog(p)"
                ></post-preview>
              </div>
              <div v-if="n === 1 && !isOwnDashboard">
                <post-preview
                  v-for="p in searchedPosts"
                  :key="p.id"
                  :post="p"
                  @click.native="showPostOpenDialog(p)"
                ></post-preview>
              </div>
              <div v-if="n === 2">
                <v-card v-for="t in 10" :key="t" hover>
                  <v-card-title tile flat>{{ t }}</v-card-title>
                  <!--<suggestion-preview></suggestion-preview>-->
                </v-card>
              </div>
              <div v-if="n === 3">
                <v-card v-for="t in 10" :key="t" hover>
                  <v-card-title tile flat>{{ t }}</v-card-title>
                  <!--<tournament-preview></tournament-preview>-->
                </v-card>
              </div>
            </v-card>
          </v-card>
        </v-col>
      </v-row>
    </v-card>
    <student-dashboard
      v-if="dashboardDialog"
      :dialog="dashboardDialog"
      :student="selectedStudent"
      :is-own-dashboard="false"
      v-on:close-dashboard-dialog="onCloseDialog"
    ></student-dashboard>
    <show-post-dialog
      v-if="currentPost"
      :dialog="postDialog"
      :post="currentPost"
      v-on:save-post="onSavePost"
      v-on:close-show-post-dialog="onCloseDialog"
    />
    <!--<show-suggestion-dialog
            v-if="currentSuggestion"
            :dialog="suggestionDialog"
            :post="currentSuggestion"
            v-on:save-post="onSaveSuggestion"
            v-on:close-show-post-dialog="onCloseDialog"
    />-->
    <!--<show-tournament-dialog
            v-if="currentTournament"
            :dialog="tournamentDialog"
            :post="currenttournament"
            v-on:save-post="onSavetournament"
            v-on:close-show-post-dialog="onCloseDialog"
    />-->
    <v-card v-if="!isReal">
      <v-card-text>The specified user does not exist :(</v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import PostPreview from '@/views/PostPreview.vue';
import Post from '@/models/management/Post';
import PostViewDialog from '@/views/PostViewDialog.vue';
import Suggestion from '@/models/management/Suggestion';
import { Tournament } from '@/models/management/Tournament';
import { Student } from '@/models/management/Student';
import StudentDashboardView from '@/views/StudentDashboardView.vue';

@Component({
  components: {
    'post-preview': PostPreview,
    'show-post-dialog': PostViewDialog,
    'student-dashboard': StudentDashboardView
  }
})
export default class DashboardHomeView extends Vue {
  @Prop({ type: Boolean, required: true })
  readonly isOwnDashboard!: boolean;
  @Prop({ type: Boolean, required: true })
  readonly isReal!: boolean;
  @Prop({ type: Array, required: false })
  readonly searchedPosts!: Post[];
  posts: Post[] = [];
  suggestions: Suggestion[] = [];
  tournaments: Tournament[] = [];
  currentPost: Post | null = null;
  currentSuggestion: Suggestion | null = null;
  currentTournament: Tournament | null = null;
  postDialog: boolean = false;
  suggestionDialog: boolean = false;
  tournamentDialog: boolean = false;
  students: Student[] = [];
  selectedStudent: Student | null = null;
  searchedStudent: string = '';
  dashboardDialog: boolean = false;

  async created() {
    this.students = await RemoteServices.getCourseStudents(
      this.$store.getters.getCurrentCourse
    );
    let ps = await RemoteServices.getPostsByUser(
      this.$store.getters.getUser.username
    );
    if (ps.lists != undefined) {
      this.posts = ps.lists;
    }
    // Falta getSuggestionsByUser e getTournamentByUser
  }

  getColumnAppBar(n: number): string {
    switch (n) {
      case 1:
        return 'Posts';
      case 2:
        return 'Suggestions';
      case 3:
        return 'Tournaments';
    }
    return '';
  }

  showPostOpenDialog(post: Post) {
    this.currentPost = post;
    this.postDialog = true;
  }

  async onSavePost(post: Post) {
    this.posts = this.posts.filter(p => p.id !== post.id);
    this.posts.unshift(post);
    this.currentPost = null;
  }

  onCloseDialog() {
    this.dashboardDialog = false;
    this.selectedStudent = null;
    this.searchedStudent = '';
    this.postDialog = false;
    // Falta fechar os dialogs das suggestions e/ou tournaments
  }

  searchForDashboard(s: string) {
    // TODO - Need to verify dashboard privacy
    let student = this.students.find(student => student.name == s);
    if (student) this.selectedStudent = student;
    else this.selectedStudent = null;
    this.dashboardDialog = true;
  }
}
</script>

<style lang="scss" scoped></style>
