<template>
  <div align="center">
    <v-card
      width="95%"
      v-if="isReal && (!this.beStudent.dashboardPrivate || this.isOwnDashboard)"
    >
      <v-row>
        <v-card-subtitle class="mx-xl-2 display-2 font-weight-black">
          Dashboard
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
        <v-row>
          <v-col class="pa-2">
            <v-card-subtitle class="text-left">
              Username: {{ beStudent.username }} <v-spacer /> Currency:
              {{ beStudent.score }} Achandos
            </v-card-subtitle>
          </v-col>
          <v-col class="pa-0" :align-self="'center'" cols="2">
            <v-switch
              class="my-0"
              v-if="isOwnDashboard"
              v-model="beStudent.dashboardPrivate"
              :label="`${textLabel}`"
              color="red"
              @change="changePrivacy()"
            />
          </v-col>
        </v-row>
      </v-card>
      <v-row scrollable no-gutters class="mx-0">
        <v-col v-for="n in 3" :key="n" cols="12" sm="4">
          <v-card tile outlined>
            <v-app-bar
              dense
              elevation="2"
              color="grey lighten-2"
              flat
              class="mx-0"
            >
              <v-toolbar-title>{{ getColumnAppBar(n) }}</v-toolbar-title>
            </v-app-bar>
            <v-card
              style="max-height: 550px"
              class="overflow-y-auto overflow-x-hidden"
              flat
            >
              <div v-if="n === 1">
                <post-preview
                  v-for="p in posts"
                  :key="p.id"
                  :post="p"
                  @click.native="showPostOpenDialog(p)"
                ></post-preview>
              </div>

              <div v-if="n === 2">
                <suggestion-preview
                  v-for="s in suggestions"
                  :key="s.id"
                  :suggestion="s"
                  @click.native="showSuggOpenDialog(s)"
                ></suggestion-preview>
              </div>

              <div v-if="n === 3">
                <tournament-preview
                  v-for="t in tournaments"
                  :key="t.id"
                  :tournament="t"
                  @click.native="showTournamentOpenDialog(t)"
                >
                </tournament-preview>
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
    <show-suggestion-dialog
      v-if="currentSuggestion"
      :dialog="suggestionDialog"
      :suggestion="currentSuggestion"
      v-on:close-show-suggestion-dialog="onCloseDialog"
    />
    <show-tournament-dialog
      v-if="currentTournament"
      :dialog="tournamentDialog"
      :tournament="currentTournament"
      :student="beStudent"
      v-on:close-show-tournament-dialog="onCloseDialog"
    />
    <v-card v-if="!isReal">
      <v-card-text>The specified user does not exist :(</v-card-text>
    </v-card>
    <v-card v-else-if="this.beStudent.dashboardPrivate && !this.isOwnDashboard">
      <v-card-text>The specified user has a private dashboard :(</v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import PostPreview from '@/views/PostPreview.vue';
import TournamentPreview from '@/views/TournamentPreview.vue';
import Post from '@/models/management/Post';
import PostViewDialog from '@/views/ShowPostDialog.vue';
import Suggestion from '@/models/management/Suggestion';
import { Tournament } from '@/models/management/Tournament';
import StudentDashboardView from '@/views/StudentDashboardView.vue';
import TournamentViewDialog from '@/views/TournamentViewDialog.vue';
import User from '@/models/user/User';
import SuggViewDialog from '@/views/ShowSuggestionDialog.vue';
import SuggsPreview from '@/views/SuggsPreview.vue';

@Component({
  components: {
    'post-preview': PostPreview,
    'tournament-preview': TournamentPreview,
    'show-post-dialog': PostViewDialog,
    'show-tournament-dialog': TournamentViewDialog,
    'student-dashboard': StudentDashboardView,
    'show-suggestion-dialog': SuggViewDialog,
    'suggestion-preview': SuggsPreview
  }
})
export default class DashboardHomeView extends Vue {
  @Prop({ type: Boolean, required: true })
  readonly isOwnDashboard!: boolean;
  @Prop({ type: Boolean, required: true })
  readonly isReal!: boolean;
  @Prop({ type: User, required: false })
  readonly student!: User;
  @Prop({ type: Array, required: false })
  readonly searchedSuggs!: Suggestion[];
  posts: Post[] = [];
  suggestions: Suggestion[] = [];
  tournaments: Tournament[] = [];
  currentPost: Post | null = null;
  currentSuggestion: Suggestion | null = null;
  currentTournament: Tournament | null = null;
  postDialog: boolean = false;
  suggestionDialog: boolean = false;
  tournamentDialog: boolean = false;
  students: User[] = [];
  selectedStudent: User | null = null;
  searchedStudent: string = '';
  dashboardDialog: boolean = false;
  beStudent: User | undefined = undefined;
  textLabel: string = '';

  async created() {
    if (this.student == null) this.beStudent = this.$store.getters.getUser;
    else this.beStudent = this.student;

    if (this.beStudent?.dashboardPrivate) this.textLabel = 'Private';
    else this.textLabel = 'Public';

    this.students = await RemoteServices.getCourseStudents(
      this.$store.getters.getCurrentCourse
    );
    if (this.beStudent?.username != null) {
      if (!this.beStudent.dashboardPrivate || this.isOwnDashboard) {
        let ps = await RemoteServices.getPostsByUser(this.beStudent.username);
        if (ps.lists != undefined) {
          this.posts = ps.lists;
        }
        this.tournaments = await RemoteServices.getTournamentsByUser(
          this.beStudent.username
        );

        let ss = await RemoteServices.getSuggestionsbyUsername(
          this.beStudent.username
        );

        if (ss._suggslist != undefined) {
          this.suggestions = ss._suggslist;
        }
      }
    }
  }
  async changePrivacy() {
    if (this.beStudent?.dashboardPrivate) this.textLabel = 'Private';
    else this.textLabel = 'Public';
    await RemoteServices.changeDashboardPrivacy();
  }

  getColumnAppBar(n: number): string {
    switch (n) {
      case 1:
        return 'Posts';
      case 2:
        return 'suggestions';
      case 3:
        return 'Tournaments';
    }
    return '';
  }

  showPostOpenDialog(post: Post) {
    this.currentPost = post;
    this.postDialog = true;
  }

  showTournamentOpenDialog(tournament: Tournament) {
    this.currentTournament = tournament;
    this.tournamentDialog = true;
    this.beStudent = this.students.find(
      student => student.username == this.beStudent?.username
    );
  }

  showSuggOpenDialog(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.suggestionDialog = true;
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
    this.tournamentDialog = false;
    this.suggestionDialog = false;
  }

  searchForDashboard(s: string) {
    let student = this.students.find(student => student.name == s);
    if (student) this.selectedStudent = student;
    else this.selectedStudent = null;
    this.dashboardDialog = true;
  }
}
</script>

<style lang="scss" scoped></style>
