<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-app-bar dense color="primary">
        <v-toolbar-title class="white--text">{{
          editSuggestion && editSuggestion.id === null
            ? 'New Suggestion'
            : 'Edit Suggestion'
        }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <toggle-button
          v-model="editSuggestion.isPrivate"
          :value="editSuggestion.isPrivate"
          :color="{ checked: 'red', unchecked: 'green' }"
          :width="75"
          :height="33"
          :name="'Make This Suggestion Private:'"
          :font-size="10"
          :labels="{ checked: 'Private', unchecked: 'Public' }"
          data-cy="togglePrivacyButton"
        />
      </v-app-bar>
      <div
        v-if="
          suggestion.status === 'REJECTED' || suggestion.status === 'TOAPPROVE'
        "
      >
        <v-card-text class="text-left" v-if="editSuggestion">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  no-resize
                  v-model="editSuggestion.title"
                  label="Title"
                  data-cy="titleTextArea"
                />
              </v-flex>
              <v-flex xs24 sm12 md12>
                <v-textarea
                  no-resize
                  outline
                  auto-grow
                  autofocus
                  counter="1024"
                  rows="4"
                  v-model="editSuggestion.studentQuestion"
                  label="Suggestion Content"
                  outlined
                  data-cy="content"
                ></v-textarea>
                <v-divider></v-divider>
              </v-flex>
              <v-flex xs24 sm12 md12>
                <v-textarea
                  no-resize
                  outline
                  auto-grow
                  autofocus
                  counter="1024"
                  rows="4"
                  v-model="editSuggestion.hint"
                  label="Hint (Optional)"
                  outlined
                  data-cy="content"
                ></v-textarea>
                <v-divider></v-divider>
              </v-flex>
              <v-flex xs24 sm12 md12>
                <v-row>
                  <v-icon color="green" class="px-3"
                    >mdi-check-circle-outline</v-icon
                  >

                  <v-textarea
                    no-resize
                    outline
                    single-line
                    rows="1"
                    v-model="editSuggestion.options[0].content"
                    color="green"
                    :label="`Correct Option`"
                    data-cy="optionTextArea"
                  ></v-textarea>
                </v-row>

                <v-row
                  v-for="index in editSuggestion.options.length - 1"
                  :key="index"
                >
                  <v-icon color="red" class="px-3"
                    >mdi-close-circle-outline</v-icon
                  >

                  <v-textarea
                    no-resize
                    outline
                    single-line
                    rows="1"
                    v-model="editSuggestion.options[index].content"
                    color="red"
                    :label="`Option ${index + 1}`"
                    data-cy="optionTextArea"
                  ></v-textarea>
                </v-row>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
        <v-divider></v-divider>
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
      </div>
      <div v-if="suggestion.status === 'APPROVED'">
        <show-suggestion
          :suggestion="this.suggestion"
          :dialog="true"
        ></show-suggestion>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="$emit('dialog', false)" data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn color="primary" @click="saveSuggestion" data-cy="saveButton"
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
import ShowSuggestion from '@/views/ShowSuggestion.vue';

Vue.use(ToggleButton);

@Component({
  components: {
    'show-suggestion': ShowSuggestion
  }
})
export default class EditSuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  editSuggestion!: Suggestion;
  student: User | null = null;

  questionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.suggestion.topicsList)
  );

  async created() {
    this.editSuggestion = new Suggestion(this.suggestion);
    this.editSuggestion.options[0].correct = true;
    this.student = await this.$store.getters.getUser;
  }

  async saveSuggestion() {
    if (this.editSuggestion && !this.editSuggestion.studentQuestion) {
      await this.$store.dispatch('error', 'Suggestion must have content');
      return;
    }

    if (this.editSuggestion && this.editSuggestion.id != null) {
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
        this.editSuggestion.student = this.$store.getters.getUser;
        this.editSuggestion.topicsList = this.questionTopics;
        const result = await RemoteServices.createSuggestion(
          this.editSuggestion
        );
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async saveTopics() {
    if (this.suggestion.id) {
      try {
        this.suggestion.topicsList = this.questionTopics;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
      'question-changed-topics',
      this.suggestion.id,
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
