<template>
  <div class="container">
    <h2>Submit Post</h2>
    <v-container class="submit-post">
      <v-container fluid>
        <v-autocomplete
                v-model="selectedQuestion"
                :items="questions"
                item-text="title"
                item-value="id"
                label="Pick a question"
                solo
                data-cy="pickQ"
        ></v-autocomplete>
      </v-container>
      <v-container fluid>
        <label class="text-area">
          <v-textarea
                  no-resize
                  v-model="message"
                  @input="checkConsistency"
                  placeholder="type your question here"
                  data-cy="typeQ"
          ></v-textarea>
        </label>
        <p class="len">{{ limit }}</p>
      </v-container>
      <v-container>
        <v-btn
                @click="submitPost"
                depressed
                color="primary"
                data-cy="submitButton"
        >
          Submit post
        </v-btn>
      </v-container>
    </v-container>
  </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Question from '@/models/management/Question';
  import { PostQuestion } from '@/models/management/PostQuestion';
  @Component
  export default class PostPostView extends Vue {
    questions: Question[] = [];
    selectedQuestion: number | null = null;
    message: string = '';
    maxlen: number = 1024;
    limit: string = this.message.length + '/' + this.maxlen;
    canSubmit: boolean = false;
    postQ: PostQuestion = new PostQuestion();
    async created() {
      await this.$store.dispatch('loading');
      try {
        this.questions = await RemoteServices.getQuestions();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
    async checkConsistency() {
      this.canSubmit = this.message.length <= this.maxlen;
      if (!this.canSubmit) {
        this.canSubmit = false;
        this.limit = 'Limit Exceeded: ' + this.message.length + '/' + this.maxlen;
      } else {
        this.limit = this.message.length + '/' + this.maxlen;
        if (this.selectedQuestion != null && this.message != '') {
          this.postQ.question = this.pickQuestion(this.selectedQuestion);
          this.postQ.studentQuestion = this.message;
          this.canSubmit = true;
        }
      }
    }
    pickQuestion(id: number) {
      let q = this.questions.find(question => question.id == id);
      if (q != undefined) return q;
      else return null;
    }
    async submitPost() {
      if (this.canSubmit) {
        try {
          await RemoteServices.submitPost(this.postQ);
          await this.$router.push({ name: 'all-posts' });
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  .submit-post {
    width: 80% !important;
    background-color: white;
    border-width: 10px;
    border-style: solid;
    border-color: #818181;
  }
  .text-area {
    width: 100%;
  }
  .len {
    text-align: right;
  }
</style>