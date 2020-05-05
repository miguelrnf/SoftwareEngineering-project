<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dashboard-dialog', false)"
    @keydown.esc="$emit('close-dashboard-dialog', false)"
  >
    <v-card>
      <dashboard-home
        class="mt-10"
        :searchedPosts="posts"
        :is-own-dashboard="false"
        :isReal="isReal"
      >
      </dashboard-home>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import { Student } from '@/models/management/Student';
import RemoteServices from '@/services/RemoteServices';
import Post from '@/models/management/Post';

@Component({
  components: {
    'dashboard-home': () => import('@/views/DashboardHomeView.vue')
  }
})
export default class StudentDashboardView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Boolean, required: true })
  readonly isOwnDashboard!: boolean;
  @Prop({ type: Student, required: false })
  readonly student!: Student;
  isReal: boolean = true;
  posts: Post[] = [];

  async created() {
    if (this.student != null) {
      if (this.student.username != null) {
        let ps = await RemoteServices.getPostsByUser(
          this.$store.getters.getUser.username
        );
        if (ps.lists != undefined) {
          this.posts = ps.lists;
        }
      }
    } else {
      this.isReal = false;
    }
    // Falta getSuggestionsByUser e getTournamentByUser
  }
}
</script>

<style scoped></style>
