<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dashboard-dialog', false)"
    @keydown.esc="$emit('close-dashboard-dialog', false)"
  >
    <v-card>
      <dashboard-home
        class="mt-10"
        :student="student"
        :is-own-dashboard="false"
        :isReal="isReal"
      >
      </dashboard-home>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import { Tournament } from '@/models/management/Tournament';
import User from '@/models/user/User';

@Component({
  components: {
    'dashboard-home': () => import('@/views/DashboardHomeView.vue')
  }
})
export default class StudentDashboardView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Boolean, required: true })
  readonly isOwnDashboard!: boolean;
  @Prop({ type: User, required: false })
  readonly student!: User;
  isReal: boolean = true;
  posts: Post[] = [];
  tournaments: Tournament[] = [];

  async created() {
    if (this.student == null) this.isReal = false;
    // Falta getSuggestionsByUser
  }
}
</script>

<style scoped></style>
