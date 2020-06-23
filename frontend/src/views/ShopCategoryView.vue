<template>
  <v-card class="mx-auto mt-4" width="95%" flat v-scroll height="95%">
    <v-row>
      <v-col cols="3">
        <v-navigation-drawer absolute permanent dense color="primary">
          <v-row class="white--text mt-5 ml-4 headline">
            <v-icon color="white" class="mr-12">fas fa-align-justify</v-icon>
            {{ 'Categories' }}
          </v-row>
          <v-divider class="mt-5 white"></v-divider>
          <v-list>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search Product"
              class="mx-2 mb-n5"
              data-cy="search"
              color="white"
            />
            <v-list-item
              v-for="item in categories"
              :key="item.title"
              @click="defineCategory(item.value)"
            >
              <v-list-item-icon>
                <v-icon color="white">{{ item.icon }}</v-icon>
              </v-list-item-icon>

              <v-list-item-content class="white--text">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>

          <template v-slot:append>
            <v-row class="white--text pa-5 ml-0" justify="center">
              {{ numHint + 'x' }}
              <v-icon color="white" class="mr-4">far fa-lightbulb</v-icon>

              {{ numFifty + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-adjust</v-icon>

              {{ numRightAns + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-check</v-icon>
            </v-row>
            <v-row class="white--text pa-5 ml-0" justify="center">
              {{ numSilvers + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-star-half-alt</v-icon>

              {{ numGolds + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-star</v-icon>

              {{ numPlatinums + 'x' }}
              <v-icon color="white" class="mr-4">fas fa-shield-alt</v-icon>
            </v-row>
            <v-row class="white--text pa-5 ml-0">
              <v-icon color="white" class="mr-12">fas fa-coins</v-icon>
              {{ $store.getters.getUser.score + ' Achandos' }}
            </v-row>
          </template>
        </v-navigation-drawer>
      </v-col>
      <v-col cols="9">
        <v-container style="max-height: 87vh" class="overflow-y-auto" fluid>
          <div v-if="type === 'All' || type === 'None'">
            <v-row>
              <v-col cols="4" v-for="item in shopItems" :key="item.name">
                <v-card outlined tile class="ma-3">
                  <v-list-item three-line>
                    <v-list-item-content>
                      <v-list-item-title class="text-left">
                        {{ item.name }}
                      </v-list-item-title>
                      <v-list-item-subtitle class="text-left">
                        {{ item.price + ' Achandos' }}
                      </v-list-item-subtitle>
                    </v-list-item-content>

                    <v-icon xx-large class="ma-4" :color="item.color">{{
                      item.icon
                    }}</v-icon>
                  </v-list-item>

                  <v-card-actions class="px-4">
                    <v-btn right color="primary" @click="buyItem(item)">
                      Buy
                    </v-btn>
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon class="ma-4 pa-0" v-on="on"
                          >far fa-question-circle</v-icon
                        >
                      </template>
                      <span>{{ item.description }}</span>
                    </v-tooltip>
                  </v-card-actions>
                </v-card>
              </v-col>
            </v-row>
          </div>
          <div v-else>
            <v-row>
              <v-col cols="4" v-for="item in categoryItems" :key="item.name">
                <v-card outlined tile class="ma-3">
                  <v-list-item three-line>
                    <v-list-item-content>
                      <v-list-item-title class="text-left">
                        {{ item.name }}
                      </v-list-item-title>
                      <v-list-item-subtitle class="text-left">
                        {{ item.price + ' Achandos' }}
                      </v-list-item-subtitle>
                    </v-list-item-content>

                    <v-icon xx-large :color="item.color" class="ma-4">{{
                      item.icon
                    }}</v-icon>
                  </v-list-item>

                  <v-card-actions class="px-4">
                    <v-btn right color="primary" @click="buyItem(item)">
                      Buy
                    </v-btn>
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon class="ma-4 pa-0" v-on="on"
                          >far fa-question-circle</v-icon
                        >
                      </template>
                      <span>{{ item.description }}</span>
                    </v-tooltip>
                  </v-card-actions>
                </v-card>
              </v-col>
            </v-row>
          </div>
        </v-container>
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import Vue2Filters from 'vue2-filters';
import { Component, Vue } from 'vue-property-decorator';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import { ShopItem } from '@/models/management/ShopItem';
import RemoteServices from '@/services/RemoteServices';
import { PostAwardItem } from '@/models/management/PostAwardItem';

@Component
export default class ShopHomeView extends Vue {
  search: string = '';
  type: string = 'None';
  numHint: number = 0;
  numFifty: number = 0;
  numRightAns: number = 0;
  userAwardsInventory: PostAwardItem[] = [];
  numSilvers: number = 0;
  numGolds: number = 0;
  numPlatinums: number = 0;

  categories = [
    { value: 'THEME', title: 'Themes', icon: 'fas fa-paint-roller' },
    { value: 'POWER_UP', title: 'Power Ups', icon: 'fas fa-rocket' },
    { value: 'POST_AWARD', title: 'Post Awards', icon: 'fas fa-star' },
    { value: 'All', title: 'All', icon: 'fas fa-ellipsis-v' }
  ];

  categoryItems: ShopItem[] = [];
  shopItems: ShopItem[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.shopItems = await RemoteServices.getShopItems();
      this.numRightAns = await RemoteServices.getNumOfPowerUp('RIGHTANSWER');
      this.numHint = await RemoteServices.getNumOfPowerUp('HINT');
      this.numFifty = await RemoteServices.getNumOfPowerUp('FIFTYFIFTY');
      this.userAwardsInventory = await RemoteServices.getAwards();
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
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async buyItem(item: ShopItem) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.buyItem(item.id);
      await this.$store.dispatch('updateUser');
      this.numRightAns = await RemoteServices.getNumOfPowerUp('RIGHTANSWER');
      this.numHint = await RemoteServices.getNumOfPowerUp('HINT');
      this.numFifty = await RemoteServices.getNumOfPowerUp('FIFTYFIFTY');
      this.userAwardsInventory = await RemoteServices.getAwards();
      this.numSilvers = 0;
      this.numGolds = 0;
      this.numPlatinums = 0;
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
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  defineCategory(type: string) {
    this.type = type;
    this.isInTheRightCategory(type);
  }

  async isInTheRightCategory(search: string) {
    if (search != '' && search != 'All') {
      this.categoryItems = this.shopItems.filter(
        item => item.type == this.type
      );
    } else {
      this.categoryItems = this.shopItems;
    }
    return this.categoryItems;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
