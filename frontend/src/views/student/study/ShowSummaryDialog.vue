<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="$emit('dialog', false)"
    @input="closeSummaryDialog"
    max-width="90%"
    max-height="100%"
  >
    <v-card>
      <div id="typography">
        <!--<div class="title">
                <h2>{{topic.name}}</h2>
            </div>-->
        <div class="md-layout">
          <div class="md-layout-item">
            <div class="tim-typo">
              <h1>
                {{ topic.name }}

              </h1>
            </div>
          </div>
        </div>
        <div class="space-50"></div>
      </div>
      <br />
      <div v-for="item in questions" :key="item.content">
          <v-card flat>

            <div
                    class="question-content"
                    v-html="convertMarkDown(item.content, item.image)"

              >              <br/>
            </div>

              <div class="overflow-hidden">


                  <v-bottom-navigation
                          :input-value="showNav"
                  >
                          <span>{{"-"}} {{getCorrect(item.options)}}</span>


                  </v-bottom-navigation>
              </div>

              <v-divider ></v-divider>


              <br />
              <br />

        </v-card>

      </div>
        <div class="text-center mb-2">
            <v-btn
                    text
                    color="primary"
                    @click="showNav = !showNav"
                    small
            >
                Show Answers
            </v-btn>
        </div>



      <v-card-actions>
        <div class="text-center">
          <v-pagination
                  v-model="page"
                  :length=topicsOrderMax
                  circle
                  @input="changeTopicPage(page)"
          ></v-pagination>
          </div>

        <v-spacer />
        <v-btn
          color="primary"
          elevation="5"
          @click="closeSummaryDialog"
          data-cy="closeButton"
          >close</v-btn
        >

      </v-card-actions>

    </v-card>

  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Option from '@/models/management/Option';

@Component
export default class ShowSummaryDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];
  questions: Question[] = [];
  topic: Topic = this.topics[0];

  topicsOrderMax: number = 0;

  async created() {
    await this.getSummary();
    this.topicsOrderMax=this.topics.length;
  }
  async getSummary() {
    if (this.topics.length != 0) {
      try {
        this.questions = await RemoteServices.getTopicQuestions(
          this.topic.name
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  closeSummaryDialog() {
    this.$emit('close-show-summary-dialog');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
    data () {
        return {
            showNav: false,
            page: 1
        }
    }

    getCorrect(options: Option[]){
      for (let i = 0; i < options.length; i++) {
        if (options[i].correct)
          return options[i].content
      }
    }

    changeTopicPage(page: number){
      this.topic = this.topics[page-1]
      this.getSummary()
    }

}

</script>

<style lang="scss"></style>
