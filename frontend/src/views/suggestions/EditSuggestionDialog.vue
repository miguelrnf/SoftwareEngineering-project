<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editSuggestion && editSuggestion._id === null
              ? 'New Suggestion'
              : 'Edit Suggestion'
          }}
        </span>
      </v-card-title>

      <h1
        v-if="
          suggestion.status === 'REJECTED' || suggestion.status === 'TOAPPROVE'
        "
      >
        <v-card-text class="text-left" v-if="editSuggestion">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="editSuggestion.title"
                  label="Title"
                  data-cy="titleTextArea"
                />
              </v-flex>
              <v-flex xs24 sm12 md12>
                <v-textarea
                  outline
                  rows="10"
                  v-model="editSuggestion._questionStr"
                  label="Content"
                  outlined
                  data-cy="content"
                ></v-textarea>
              </v-flex>
              <v-flex
                xs24
                sm12
                md12
                v-for="index in editSuggestion.options.length"
                :key="index"
              >
                <v-switch
                  v-model="editSuggestion.options[index - 1].correct"
                  class="ma-4"
                  label="Correct"
                  data-cy="correctToggleButton"
                />
                <v-textarea
                  outline
                  rows="10"
                  v-model="editSuggestion.options[index - 1].content"
                  :label="`Option ${index}`"
                  data-cy="optionTextArea"
                ></v-textarea>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
        <v-card-text>
          <v-autocomplete
            v-model="questionTopics"
            :items="topics"
            multiple
            return-object
            item-text="name"
            item-value="name"
            label="Topics"
            outlined
            @change="saveTopics"
            data-cy="topics"
          >
            <template v-slot:selection="data">
              <v-chip
                v-bind="data.attrs"
                :input-value="data.selected"
                close
                @click="data.select"
                @click:close="removeTopic(data.item)"
              >
                {{ data.item.name }}
              </v-chip>
            </template>
            <template v-slot:item="data">
              <v-list-item-content>
                <v-list-item-title v-html="data.item.name" />
              </v-list-item-content>
            </template>
          </v-autocomplete>
        </v-card-text>
      </h1>

      <h1 v-if="suggestion.status === 'APPROVED'">
        <v-card class="mx-auto" max-height="80%">
          <v-app-bar dense color="grey lighten-2">
            <v-row>
              <v-card-title class="mt-n2 ml-3">{{
                'Suggestion' + suggestion._id
              }}</v-card-title>

              <v-spacer />
              <div class="mr-6 mt-3">
                <v-chip
                  class="ma-1"
                  x-small
                  label
                  :color="getColor1(suggestion._isprivate)"
                  text-color="white"
                  dark
                  ><span class="white--text ">{{
                    getPrivacyTag(suggestion._isprivate)
                  }}</span></v-chip
                >
                <v-chip
                  class="ma-1"
                  x-small
                  label
                  :color="getColor2(suggestion.status)"
                  dark
                  ><span class="white--text ">{{
                    suggestion.status
                  }}</span></v-chip
                >
              </div>
            </v-row>
          </v-app-bar>

          <v-card-text>
            <p class="headline font-weight-black text-left">
              <span v-html="convertMarkDown(suggestion.title)" />
            </p>
            <div class="headline text-left">
              <span v-html="convertMarkDown(suggestion._questionStr)" />
            </div>
            <v-row>
              <span v-html="convertMarkDown('Options: ')" />
              <v-chip
                v-for="option in suggestion.options"
                :key="option.id"
                class="ma-1"
                x-small
                :color="getChipColor(option.correct)"
                outlined
                :text-color="getTextColor(option.correct)"
                dark
                >{{ option.content }}
              </v-chip>
            </v-row>
            <v-row>
              <span v-html="convertMarkDown('Topics: ')" />
              <v-chip
                v-for="option in suggestion._topicsList"
                :key="option.id"
                class="ma-1"
                x-small
                color="grey"
                text-color="white"
                dark
                ><span class="white--text">{{ option.name }}</span>
              </v-chip>
            </v-row>
            <div class="text-right">
              by
              <span
                v-html="
                  convertMarkDown(
                    suggestion._student.username +
                      ' on ' +
                      suggestion.creationDate
                  )
                "
              />
            </div>
          </v-card-text>
        </v-card>
      </h1>

      <v-subheader>Make Your Suggestion Private:</v-subheader>
      <v-card-actions>
        <toggle-button
          v-model="editSuggestion._isprivate"
          :value="false"
          :color="{ checked: 'red', unchecked: 'green' }"
          :width="75"
          :height="33"
          :name="'Make This Suggestion Private:'"
          :font-size="10"
          :labels="{ checked: 'Private', unchecked: 'Public' }"
          data-cy="togglePrivacyButton"
        />
      </v-card-actions>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveSuggestion"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Suggestion from '@/models/management/Suggestion';
import Topic from '@/models/management/Topic';
import ToggleButton from 'vue-js-toggle-button';
import User from '@/models/user/User';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

Vue.use(ToggleButton);

@Component
export default class EditSuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  editSuggestion!: Suggestion;
  student: User | null = null;

  questionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.suggestion._topicsList)
  );

  async created() {
    this.editSuggestion = new Suggestion(this.suggestion);
    this.student = await this.$store.getters.getUser;
  }

  async saveSuggestion() {
    if (this.editSuggestion && !this.editSuggestion._questionStr) {
      await this.$store.dispatch('error', 'Suggestion must have content');
      return;
    }

    if (this.editSuggestion && this.editSuggestion._id != null) {
      try {
        const result = await RemoteServices.updateSuggestion(
          this.editSuggestion
        );
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editSuggestion) {
      try {
        this.editSuggestion._questionStr = this.editSuggestion._questionStr;
        this.editSuggestion._student = this.$store.getters.getUser;
        this.editSuggestion._topicsList = this.questionTopics;
        this.editSuggestion._isprivate = this.editSuggestion._isprivate;
        const result = await RemoteServices.createSuggestion(
          this.editSuggestion
        );
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  getChipColor(iscorrect: boolean) {
    if (iscorrect) return 'green';
    return 'red';
  }

  getTextColor(iscorrect: boolean) {
    if (iscorrect) return 'green';
    return 'red';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getPrivacyTag(isprivate: boolean) {
    if (isprivate) return 'PRIVATE';
    else return 'PUBLIC';
  }

  getColor1(IsPrivate: boolean) {
    let vazo = 'black';
    if (IsPrivate) return vazo;
    else return 'orange';
  }

  getColor2(Status: string) {
    if (Status == 'TOAPPROVE') return 'yellow';
    else if (Status == 'REJECTED') return 'red';
    else return 'green';
  }

  async saveTopics() {
    if (this.suggestion._id) {
      try {
        this.suggestion._topicsList = this.questionTopics;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
      'question-changed-topics',
      this.suggestion._id,
      this.questionTopics
    );
  }

  removeTopic(topic: Topic) {
    this.questionTopics = this.questionTopics.filter(
      element => element.id != topic.id
    );
    this.saveTopics();
  }
}
</script>
