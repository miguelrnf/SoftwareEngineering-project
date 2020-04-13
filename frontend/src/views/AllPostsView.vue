<template>
  <div class="container">
    <h2>Available Quizzes</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Question</div>
        <div class="col">User</div>
      </li>
      <li class="list-row" v-for="posta in allp.lists" :key="posta.id">
        <div class="col">
          {{ posta.question.question.title }}
        </div>
        <div class="col">
          {{ getSmallQuestion(posta.question.studentQuestion) }}
        </div>
        <div class="col">
          {{ posta.question.user.username }}
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ListPost from '@/models/management/ListPost';

@Component
export default class AllPostsView extends Vue {
  allp: ListPost = new ListPost();

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.allp = await RemoteServices.viewPosts(15, 1);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getSmallQuestion(question: string) {
    if (question.length > 30) {
      return question.slice(0, 30).concat(' . . .');
    } else {
      return question;
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 100% !important;
      margin: auto; /* Important */
      width: 33%;
      height: auto;
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>