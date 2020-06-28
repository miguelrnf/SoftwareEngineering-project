<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card class="px-12 ma-0 table" height="60%">
      <v-row>
        <v-card-title color="primary" class="mb-5 table">
          <v-icon left>fas fa-plus</v-icon>
          {{ getDocumentTypeCaps() }}
        </v-card-title>
      </v-row>
      <v-row>
        <v-textarea
          v-if="type === 'New Video'"
          :label="getDocumentLabel()"
          v-model="editDocument.title"
          auto-grow
          outlined
          rows="5"
          row-height="15"
        ></v-textarea>
        <v-textarea
          v-if="type === 'New Document'"
          :label="getDocumentLabel()"
          v-model="editDocument.title"
          auto-grow
          outlined
          rows="5"
          row-height="15"
        ></v-textarea>
      </v-row>

      <v-row>
        <v-textarea
          v-if="type === 'New Video'"
          label="Write Video Url Here"
          v-model="editDocument.url"
          auto-grow
          outlined
          rows="1"
          row-height="15"
        ></v-textarea>

        <div v-if="type === 'New Document'">
          <v-textarea
            v-if="type === 'New Document'"
            label="Write Document Content Here"
            v-model="editDocument.content"
            auto-grow
            outlined
            rows="5"
            row-height="15"
          ></v-textarea>

          <div id="app">
            <div class="container">
              <form
                enctype="multipart/form-data"
                novalidate
                v-if="currentStatus === 0 || currentStatus === 1"
              >
                <h3>Upload file</h3>
                <div class="dropbox">
                  <input
                    type="file"
                    multiple
                    :name="uploadFieldName"
                    :disabled="currentStatus === 1"
                    @change="
                      filesChange($event.target.name, $event.target.files)
                    "
                    class="input-file"
                  />
                  <p v-if="currentStatus === 0">
                    Drag your file here to begin<br />
                    or click to browse (20 Mb max size)
                  </p>
                  <p v-if="currentStatus === 1">
                    Uploading file...
                  </p>
                </div>
              </form>
              <!--SUCCESS-->
              <div v-if="currentStatus === 2">
                <h2>Uploaded file successfully.</h2>
                <p>
                  <a href="javascript:void(0)" @click="reset()">Upload again</a>
                </p>
              </div>
              <!--FAILED-->
              <div v-if="currentStatus === 3">
                <h2>Uploaded failed.</h2>
                <p>
                  <a href="javascript:void(0)" @click="reset()">Try again</a>
                </p>
                <pre>{{ uploadError }}</pre>
              </div>
            </div>
          </div>
        </div>
      </v-row>
      <v-row>
        <v-btn
          v-if="type === 'New Video'"
          text
          color="primary"
          @click="previewVideo()"
          >Preview</v-btn
        >
      </v-row>
      <v-container class="test">
        <LazyYoutubeVideo
          v-if="preview && type === 'New Video'"
          :src="previewVideo()"
          preview-image-size="sddefault"
        />
      </v-container>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          v-if="type !== 'New Document'"
          color="primary"
          text
          @click="saveDocument"
          data-cy="saveButton"
          >Save</v-btn
        >
        <v-btn
          v-if="type === 'New Document'"
          color="primary"
          text
          @click="savePdf"
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
  import ToggleButton from 'vue-js-toggle-button';
  import Image from '@/models/management/Image';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import { getIdFromURL } from 'vue-youtube-embed';
  import { LazyYoutubeVideo } from 'vue-lazy-youtube-video';
  import 'vue-lazy-youtube-video/dist/style.css';
  import Classroom from '@/models/management/Classroom';
  import Document from '@/models/management/Document';

  const STATUS_INITIAL = 0,
  STATUS_SAVING = 1,
  STATUS_SUCCESS = 2,
  STATUS_FAILED = 3;

Vue.use(ToggleButton);

@Component({
  components: {
    LazyYoutubeVideo
  }
})
export default class EditDocumentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Document, required: true }) readonly document!: Document;
  @Prop({ type: String, required: true }) readonly type!: String;
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;

  editDocument!: Document;

  preview: boolean = false;
  videoId: string = '';
  videoBase: string = 'https://www.youtube.com/embed/';

  currentStatus = 0;
  uploadError: null = null;
  uploadFieldName: string = 'photos';

  reset() {
    // reset form to initial state
    this.currentStatus = STATUS_INITIAL;
    this.uploadError = null;
  }

  async save(formData: FormData, name: string) {
    // upload data to the server

    this.editDocument.classroomId = this.lecture.id;
    this.editDocument.type = 'DOC';
    let f = formData.get(name) as File;
    this.editDocument.extension = f.name;

    try {
      this.currentStatus = STATUS_SAVING;

      const doc = await RemoteServices.createDocument(this.editDocument);

      await RemoteServices.uploadDoc(formData, doc);

      this.currentStatus = STATUS_SUCCESS;
    } catch (error) {
      await this.$store.dispatch('error', error);
      this.uploadError = error.response;
      this.currentStatus = STATUS_FAILED;
    }
  }

  filesChange(fieldName: string, fileList: File[]) {
    // handle file changes
    const formData = new FormData();

    if (!fileList.length) return;

    // append the files to FormData
    Array.from(Array(fileList.length).keys()).map(x => {
      formData.append(fieldName, fileList[x], fileList[x].name);
    });

    // save it
    this.save(formData, fieldName);
  }

  async created() {
    this.editDocument = new Document(this.document);
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');
    this.currentStatus = STATUS_INITIAL;
  }

  getDocumentTypeCaps() {
    if (this.type === 'New Document') {
      return 'DOCUMENT';
    } else {
      return 'VIDEO';
    }
  }

  async saveDocument() {
    this.editDocument.url = this.previewVideo();

    if (this.editDocument && this.editDocument.id != null) {
      try {
        const result = await RemoteServices.editDocument(this.editDocument);
        this.$emit('save-document', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editDocument) {
      this.editDocument.classroomId = this.lecture.id;

      try {
        if (this.type === 'New Document') {
          this.editDocument.type = 'DOC';
        } else {
          this.editDocument.type = 'VIDEO';
        }

        const result = await RemoteServices.createDocument(this.editDocument);

        this.$emit('save-document', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  savePdf() {
    this.$emit('save-document', this.editDocument);
  }

  previewVideo(): string {
    this.videoId = getIdFromURL(this.editDocument.url);
    let tempvar = this.videoBase;
    tempvar = tempvar + this.videoId;
    this.preview = true;
    return tempvar;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getDocumentLabel() {
    if (this.type === 'New Document') {
      return 'Write Document Title Here';
    } else {
      return 'Write Video Title Here';
    }
  }
}
</script>
<style lang="scss" scoped>
$gap: 20px;

.test {
  justify-content: center;
  width: 30%;
}

.dropbox {
  outline: 2px dashed grey; /* the dash box */
  outline-offset: -10px;
  background: lightcyan;
  color: dimgray;
  padding: 10px 10px;
  min-height: 200px; /* minimum height */
  position: relative;
  cursor: pointer;
}

.input-file {
  opacity: 0; /* invisible but it's there! */
  width: 100%;
  height: 200px;
  position: absolute;
  cursor: pointer;
}

.dropbox:hover {
  background: lightblue; /* when mouse over to the drop zone, change color */
}

.dropbox p {
  font-size: 1.2em;
  text-align: center;
  padding: 50px 0;
}
</style>
