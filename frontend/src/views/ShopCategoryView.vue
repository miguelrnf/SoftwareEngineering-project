<template>
  <v-card
    class="mx-auto mt-4 d-flex flex-wrap"
    width="95%"
    height="65%"
    max-height="95%"
    flat
    v-scroll
  >
    <v-row>
      <v-col md="2" class="mr-n12 mt-n2">
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
              @click="defineCategory(item.title)"
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
            <v-row class="white--text pa-5 ml-0">
              <v-icon color="white" class="mr-12">fas fa-coins</v-icon>
              {{ $store.getters.getUser.score + ' Achandos' }}
            </v-row>
          </template>
        </v-navigation-drawer>
      </v-col>
      <v-col md="10" class="ml-6">
        <v-content>
          <v-container fluid>
            <div v-if="category === 'All' || category === 'None'">
              <v-row>
                <v-col
                  cols="3"
                  md="4"
                  v-for="item in items"
                  :custom-filter="customFilter"
                  :key="item.name"
                >
                  <v-card outlined tile height="237px" width="600px">
                    <v-icon x-large class="mt-12 mb-12">{{ item.icon }}</v-icon>
                    <v-divider class="mt-10 mb-n2"></v-divider>
                    <v-card-actions>
                      <v-card-title class="pa-0" style="font-size: large">{{
                        item.name
                      }}</v-card-title>
                      <v-spacer />
                      <v-card-subtitle>{{
                        item.price + ' Achandos'
                      }}</v-card-subtitle>
                      <v-btn @click="applyTheme(n)" color="primary">
                        Buy
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-col>
              </v-row>
            </div>
            <div v-else>
              <v-row>
                <v-col
                  cols="3"
                  md="4"
                  v-for="item in categoryItems"
                  :custom-filter="customFilter"
                  :key="item.name"
                >
                  <v-card outlined tile height="237px" width="600px">
                    <v-icon x-large class="mt-12 mb-12">{{ item.icon }}</v-icon>
                    <v-divider class="mt-10 mb-n2"></v-divider>
                    <v-card-actions>
                      <v-card-title class="pa-0" style="font-size: large">{{
                        item.name
                        }}</v-card-title>
                      <v-spacer />
                      <v-card-subtitle>{{
                        item.price + ' Achandos'
                        }}</v-card-subtitle>
                      <v-btn @click="applyTheme(n)" color="primary">
                        Buy
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-col>
              </v-row>
            </div>
          </v-container>
        </v-content>
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import showitemdialog from '@/views/showitemdialog.vue';

@Component
export default class ShopHomeView extends Vue {
  search: string = '';
  category: string = 'None';

  categories = [
    { title: 'Themes', icon: 'fas fa-paint-roller' },
    { title: 'Power Ups', icon: 'fas fa-rocket' },
    { title: 'Post Awards', icon: 'fas fa-star' },
    { title: 'All', icon: 'fas fa-ellipsis-v' }
  ];
  items = [
    {
      name: 'YAO MING',
      icon: 'fas fa-viruses',
      color: 'grey',
      category: 'Themes',
      price: '69'
    },
    {
      name: 'MANUTE BOL',
      icon: 'fas fa-carrot',
      color: 'ay',
      category: 'Themes',
      price: '169'
    },
    {
      name: 'CHING CHONG',
      icon: 'fas fa-pepper-hot',
      color: 'ay',
      category: 'Themes',
      price: '1169'
    },
    {
      name: 'KOBE BRYANT',
      icon: 'fas fa-cross',
      color: 'ay',
      category: 'Themes',
      price: '6969'
    },
    {
      name: 'LEBRON JAMES',
      icon: 'fab fa-apple',
      color: 'ay',
      category: 'Themes',
      price: '116969'
    },
    {
      name: 'SHAQ',
      icon: 'fas fa-cannabis',
      color: 'ay',
      category: 'Themes',
      price: '6'
    },
    {
      name: 'Lil Uzi Vert',
      icon: 'fas fa-toilet',
      color: 'ay',
      category: 'Power Ups',
      price: '9'
    },
    {
      name: 'Playboi Carti',
      icon: 'fas fa-toilet-paper-slash',
      color: 'ay',
      category: 'Power Ups',
      price: '69'
    },
    {
      name: 'Lil Pump',
      icon: 'fas fa-hand-middle-finger',
      color: 'ay',
      category: 'Power Ups',
      price: '269'
    },
    {
      name: 'Ricardo',
      icon: 'fas fa-cookie-bite',
      color: 'ay',
      category: 'Post Awards',
      price: '0'
    },
    {
      name: 'Daniel',
      icon: 'fas fa-wine-bottle',
      color: 'ay',
      category: 'Post Awards',
      price: '0'
    },
    {
      name: 'Henriques',
      icon: 'fas fa-glass-cheers',
      color: 'ay',
      category: 'Post Awards',
      price: '0'
    },
    {
      name: 'Vazo',
      icon: 'fas fa-cocktail',
      color: 'ay',
      category: 'Post Awards',
      price: '0'
    }
  ];

  categoryItems: object[] = [];

  defineCategory(title: string) {
    this.category = title;
    this.isInTheRightCategory(title);
  }

  async isInTheRightCategory(search: string) {
    if (search != '' && search != 'All') {
      this.categoryItems = this.items.filter(
        item => item.category == this.category
      );
    } else {
      this.categoryItems = this.items;
    }
    return this.categoryItems;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
