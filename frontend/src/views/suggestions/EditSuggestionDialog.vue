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

      <h1 v-if="suggestion.status === 'REJECTED' || suggestion.status === 'TOAPPROVE'">
        <v-card-text class="text-left" v-if="editSuggestion">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
              <v-text-field v-model="editSuggestion.title" label="Title" />
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
              />
              <v-textarea
                      outline
                      rows="10"
                      v-model="editSuggestion.options[index - 1].content"
                      :label="`Option ${index}`"
              ></v-textarea>
            </v-flex>-->
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

      <h1
        v-if="
          suggestion.status === 'APPROVED'        "
      >
        <v-subheader>Creation Date:</v-subheader>
        <v-card-text class="text-left">
          <span>{{ suggestion.creationDate }}</span>
        </v-card-text>

        <v-subheader>Student Username:</v-subheader>
        <v-card-text class="text-left">
          <span>{{ student.username }}</span>
        </v-card-text>

        <v-subheader>Question:</v-subheader>
        <v-card-text class="text-left">
          <span>{{ suggestion._questionStr }}</span>
        </v-card-text>

        <v-subheader>Topics:</v-subheader>
        <ul>
          <li v-for="option in suggestion._topicsList" :key="option.id">
            <span class="text-left">{{ option.name }}</span>
          </li>
        </ul>
        <br />
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
import { Student } from '@/models/management/Student';
import User from '@/models/user/User';
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
