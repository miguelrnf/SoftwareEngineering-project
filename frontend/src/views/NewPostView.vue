<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-new-post-dialog', false)"
    @keydown.esc="$emit('close-new-post-dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-app-bar dense color="primary">
        <v-toolbar-title class="white--text">{{
          'Submit Post'
        }}</v-toolbar-title></v-app-bar
      >
      <v-container grid-list-md fluid>
        <v-card-text>
          <v-autocomplete
            v-model="selectedQuestion"
            :items="questions"
            item-text="title"
            item-value="id"
            label="Pick a question"
            solo
            data-cy="pickQ"
          ></v-autocomplete>
        </v-card-text>

        <div v-if="selectedQuestion !== null" class="ml-4 mt-n7 mb-4 mr-4">
          <v-card outlined>
            <v-card-text class="text-left">{{
              pickQuestion(this.selectedQuestion).content
            }}</v-card-text>
          </v-card>
        </div>

        <v-divider></v-divider>

        <v-card-text>
          <label class="text-area">
            <v-textarea
              no-resize
              outlined
              counter="1024"
              v-model="message"
              placeholder="type your question here"
              data-cy="typeQ"
            ></v-textarea>
          </label>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer />

          <v-btn
            color="primary"
            text
            @click="$emit('close-new-post-dialog', false)"
            data-cy="cancel"
            >Cancel</v-btn
          ><v-btn color="green" text @click="submitPost" data-cy="submitButton">
            Submit post
          </v-btn>
        </v-card-actions>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import { PostQuestion } from '@/models/management/PostQuestion';
@Component
export default class NewPostView extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
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

  checkConsistency() {
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

    this.checkConsistency();

    if (this.canSubmit) {
      try {
        let p = await RemoteServices.submitPost(this.postQ);
        //await this.$router.push({ name: 'all-posts' });
        this.$emit('save-post', p);
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
