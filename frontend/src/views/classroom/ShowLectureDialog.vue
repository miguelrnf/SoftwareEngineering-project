<template>
  <v-dialog
    :value="dialog"
    @input="closeLectureDialog"
    @keydown.esc="closeLectureDialog"
    max-width="100%"
  >
    <v-card max-height="85%" class="px-5">
      <v-row>
        <v-card-title color="primary" class="mb-5 table">
          <v-icon left>{{ getLectureTypeIcon() }}</v-icon>
          {{ getLectureTypeCaps() + ' - ' + this.lecture.title }}
        </v-card-title>
      </v-row>
      <v-tabs>
        <v-tab @click="setTabName('New Document')">
          <v-icon left>far fa-bookmark</v-icon>
          DOCUMENTATION
        </v-tab>

        <v-tab @click="setTabName('New Video')">
          <v-icon left>fas fa-play</v-icon>
          VIDEOS
        </v-tab>
        <v-tab @click="setTabName('New Quiz')">
          <v-icon left>ballot</v-icon>
          QUIZZES
        </v-tab>
        <v-spacer />
        <v-btn
          color="primary"
          class="mr-6"
          v-if="teacher && doctype !== 'New Quiz'"
          @click="newDocument"
          >{{ this.doctype }}</v-btn
        >
        <v-btn
          color="primary"
          class="mr-6"
          v-if="teacher && doctype === 'New Quiz'"
          @click="newQuizSelection"
          >{{ this.doctype }}</v-btn
        >
        <v-tab-item>
          <v-list>
            <v-list-group v-for="d in documents" :key="d.title">
              <template v-slot:activator>
                <v-list-item-content>
                  <v-list-item-title v-text="d.title" />
                </v-list-item-content>
              </template>

              <v-list-item color="primary">
                <v-list-item-content>
                  <v-list-item-title v-text="d.content" />
                </v-list-item-content>

                <v-icon @click="clickPdf(d)" color="primary"
                  >fas fa-file-download</v-icon
                >
                <v-icon v-if="teacher" @click="editDocument(d)" color="primary"
                  >edit</v-icon
                >
                <v-icon v-if="teacher" @click="deleteDocument(d)" color="error"
                  >delete</v-icon
                >
              </v-list-item>
            </v-list-group>
          </v-list>
        </v-tab-item>
        <v-tab-item>
          <v-list>
            <v-list-group v-for="v in videos" :key="v.title">
              <template v-slot:activator>
                <v-list-item-content>
                  <v-list-item-title v-text="v.title" />
                </v-list-item-content>
              </template>

              <v-list-item color="primary">
                <v-container class="test">
                  <LazyYoutubeVideo
                    :src="v.url"
                    preview-image-size="sddefault"
                  />
                </v-container>

                <v-icon v-if="teacher" @click="editDocument(v)" color="primary"
                  >edit</v-icon
                >
                <v-icon v-if="teacher" @click="deleteDocument(v)" color="error"
                  >delete</v-icon
                >
              </v-list-item>
            </v-list-group>
          </v-list>
        </v-tab-item>
        <v-tab-item v-if="teacher">
          <v-data-table
            :headers="headers"
            :custom-filter="customFilter"
            :items="selectedQuizzes"
            :search="search"
            multi-sort
            :mobile-breakpoint="0"
            :items-per-page="15"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
            <template v-slot:top>
              <v-card-title>
                <v-spacer />
                <v-spacer />
                <v-text-field
                  v-model="search"
                  append-icon="search"
                  label="Search Quizzes"
                  class="mx-2"
                  data-cy="search"
                />
              </v-card-title>
            </template>

            <template v-slot:item.action="{ item }">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    color="red"
                    @click="removeQuiz(item.id)"
                    >delete</v-icon
                  >
                </template>
                <span>Delete Quiz</span>
              </v-tooltip>

              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    color="primary"
                    @click="setEvaluation(item, true)"
                    >fas fa-check</v-icon
                  >
                </template>
                <span>Set Quiz to Evaluation</span>
              </v-tooltip>

              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    color="primary"
                    @click="setEvaluation(item, false)"
                    >fas fa-window-close</v-icon
                  >
                </template>
                <span>Unset Quiz to Evaluation</span>
              </v-tooltip>
            </template>
          </v-data-table>
        </v-tab-item>
        <v-tab-item v-else>
          <v-list three-line>
            <v-row>
              <v-col>
                <div class="col">
                  Title
                </div>
              </v-col>

              <v-col>
                <div class="col">Available since</div>
              </v-col>
            </v-row>
            <v-list-item-group>
              <template v-for="(l, index) in selectedQuizzes">
                <v-list-item :key="l.title" @click="startQuiz(l)">
                  <template>
                    <v-list-item-content>
                      <v-row>
                        <v-col>
                          <v-list-item-title class="test" v-text="l.title" />
                        </v-col>
                        <v-col>
                          <v-list-item-subtitle
                            class="text--primary"
                            v-text="l.availableDate"
                          />
                        </v-col>
                      </v-row>
                    </v-list-item-content>
                  </template>
                </v-list-item>

                <v-divider
                  v-if="index + 1 < selectedQuizzes.length"
                  :key="index"
                />
              </template>

              <template v-for="(l, index) in solvedQuizzes">
                <v-list-item :key="l.title" @click="show(l)">
                  <template>
                    <v-list-item-content>
                      <v-row>
                        <v-col>
                          <v-list-item-title
                            class="test"
                            v-text="l.statementQuiz.title"
                          />
                        </v-col>
                        <v-col>
                          <v-list-item-subtitle
                            class="text--primary"
                            v-text="l.statementQuiz.availableDate"
                          />
                        </v-col>
                      </v-row>
                    </v-list-item-content>
                  </template>
                </v-list-item>

                <v-divider
                  v-if="index + 1 < solvedQuizzes.length"
                  :key="index"
                />
              </template>
            </v-list-item-group>
          </v-list>
        </v-tab-item>
      </v-tabs>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" text @click="closeLectureDialog">
          Close
        </v-btn>
      </v-card-actions>
      <edit-document-dialog
        v-if="current && newOrEditDialog"
        v-model="newOrEditDialog"
        :document="current"
        :type="doctype"
        :dialog="newOrEditDialog"
        :lecture="this.lecture"
        v-on:save-document="onSaveDocument"
      />
      <edit-selected-quizzes-dialog
        v-if="lec && selectQuizBoolean"
        v-model="selectQuizBoolean"
        :available-quizzes="availableQuizzes"
        :lecture="this.lecture"
        v-on:save-selected-quizzes="onSaveQuizzes"
      />
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Classroom from '@/models/management/Classroom';
import Document from '@/models/management/Document';
import EditDocumentDialog from '@/views/classroom/EditDocumentDialog.vue';
import LazyYoutubeVideo from 'vue-lazy-youtube-video';
import StatementQuiz from '@/models/statement/StatementQuiz';
import EditSelectedQuizzesDialog from '@/views/classroom/EditSelectedQuizzesDialog.vue';
import StatementManager from '@/models/statement/StatementManager';
import SolvedQuiz from '@/models/statement/SolvedQuiz';

@Component({
  components: {
    'edit-document-dialog': EditDocumentDialog,
    'edit-selected-quizzes-dialog': EditSelectedQuizzesDialog,
    LazyYoutubeVideo
  }
})
export default class ShowLectureDialog extends Vue {
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: String, required: true }) readonly type!: String;
  @Prop({ type: Boolean, required: true }) readonly teacher!: boolean;

  availableQuizzes: StatementQuiz[] | null = null;

  doctype: string = 'New Document';

  selectQuizBoolean: boolean = false;
  videos: Document[] = [];
  documents: Document[] = [];
  lec!: Classroom;
  selectedQuizzes: StatementQuiz[] | null = null;
  solvedQuizzes: SolvedQuiz[] | null = null;
  search: string = '';
  statementManager: StatementManager = StatementManager.getInstance;

  newOrEditDialog: boolean = false;
  current: Document | null = null;
  headers: object = [
    {
      text: 'Title',
      align: 'center',
      value: 'title',
      sortable: false
    },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'center'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];
  @Watch('newOrEditDialog')
  closeError() {
    if (!this.newOrEditDialog) {
      this.current = null;
    }
  }

  async created() {
    this.statementManager.reset();
    this.documents = this.lecture.documents.filter(d => d.type != 'VIDEO');

    this.videos = this.lecture.documents.filter(d => d.type != 'DOC');

    this.lec = new Classroom(this.lecture);
    this.selectedQuizzes = await RemoteServices.getClassroomQuizzes(this.lec);
    this.solvedQuizzes = await RemoteServices.getClassroomSolvedQuizzes(
      this.lec
    );

    this.availableQuizzes = await RemoteServices.getAvailableQuizzes();
  }

  async removeQuiz(quizId: number) {
    this.lec = await RemoteServices.deleteClassroomQuiz(this.lec.id, quizId);
    this.availableQuizzes = await RemoteServices.getAvailableQuizzes();
  }

  async show(quiz: SolvedQuiz) {
    this.statementManager.correctAnswers = quiz.correctAnswers;
    this.statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ path: '/student/results' });
  }

  closeLectureDialog() {
    this.$emit('close-show-lecture-dialog');
  }

  async startQuiz(quiz: StatementQuiz) {
    try {
      this.statementManager.statementQuiz = quiz;
      await this.$router.push({ path: '/student/quiz' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async deleteDocument(document: Document) {
    if (
      document.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteDocument(this.lec.id, document.id);

        if (this.doctype == 'New Document') {
          this.documents = this.documents.filter(l => l.id != document.id);
        } else {
          this.videos = this.videos.filter(l => l.id != document.id);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async setEvaluation(q: StatementQuiz, bool: boolean) {
    q.evaluation = bool;
    await RemoteServices.setEvaluation(this.lec, q);
  }
  customFilter(value: string, search: string, lecture: Classroom) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(lecture)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }
  getLectureTypeCaps() {
    if (this.type === 'New Lecture') {
      return 'LECTURE';
    } else if (this.type === 'New Lab') {
      return 'LAB';
    } else {
      return 'PROJECT';
    }
  }

  editDocument(doc: Document) {
    this.current = doc;
    this.newOrEditDialog = true;
  }

  newDocument() {
    this.current = new Document();
    this.newOrEditDialog = true;
  }

  newQuizSelection() {
    this.selectQuizBoolean = true;
  }

  async onSaveQuizzes() {
    this.selectedQuizzes = await RemoteServices.getClassroomQuizzes(this.lec);
    this.selectQuizBoolean = false;
  }
  async onSaveDocument(doc: Document) {
    if (this.doctype === 'New Document') {
      this.documents = this.documents.filter(d => d.id != doc.id);
      this.documents.unshift(doc);
    } else {
      this.videos = this.videos.filter(v => v.id != doc.id);
      this.videos.unshift(doc);
    }
    this.newOrEditDialog = false;
    this.current = null;
  }

  getLectureTypeIcon() {
    if (this.type === 'New Lecture') {
      return 'fab fa-leanpub';
    } else if (this.type === 'New Lab') {
      return 'fas fa-laptop-code';
    } else {
      return 'fab fa-git-alt';
    }
  }
  setTabName(str: string) {
    this.doctype = str;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async clickPdf(response: Document) {
    const result = await RemoteServices.getDoc(response);
    let binaryString = window.atob(result as string);

    let binaryLen = binaryString.length;

    let bytes = new Uint8Array(binaryLen);

    for (let i = 0; i < binaryLen; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }

    let blob = new Blob([bytes], { type: 'application/octet-stream' });

    let link = document.createElement('a');

    link.href = window.URL.createObjectURL(blob);
    link.download = response.title + '.' + response.extension;

    link.click();
  }
}
</script>

<style lang="scss" scoped>
$gap: 20px;

.test {
  justify-content: center;
  width: 50%;
}

.test2 {
  padding-right: 60px;
}
</style>
