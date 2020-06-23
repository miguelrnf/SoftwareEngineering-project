<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-buy-awards-dialog', false)"
    @keydown.esc="$emit('close-buy-awards-dialog', false)"
    max-width="25%"
    max-height="80%"
  >
      <v-row>
          <v-badge
                  class="font-weight-bold"
                  offset-y="30"
                  color=""
                  :content="'x' + "
          ><v-icon :color="award.award.item.color" small>{{
              award.award.item.icon
              }}</v-icon>
          </v-badge>
      </v-row>
    <v-card>
      <v-card-actions class="pb-5 pt-0 px-5">
        <v-spacer />
        <v-btn
          class="mt-2 white--text"
          color="primary"
          @click="$emit('close-buy-awards-dialog')"
          >Cancel</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AwardPostDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  userAwardsInventory: PostAwardItem[] = [];
  numSilvers: number = 0;
  numGolds: number = 0;
  numPlatinums: number = 0;

  async shopRedirect() {
    await this.$router.push({ name: 'shop-home' });
  }

  async getAwards() {
    for (let i = 0; i < this.userAwardsInventory.length; i++) {
      if (this.userAwardsInventory[i].type == 'SILVER') {
        this.numSilvers = this.numSilvers + 1;
      }
      if (this.userAwardsInventory[i].type == 'GOLD') {
        this.numGolds = this.numGolds + 1;
      }
      if (this.userAwardsInventory[i].type == 'PLATINUM') {
        this.numPlatinums = this.numPlatinums + 1;
      }
    }
  }

  async created() {
    this.userAwardsInventory = await RemoteServices.getAwards();
    this.getAwards();
  }
}
</script>

<style></style>
