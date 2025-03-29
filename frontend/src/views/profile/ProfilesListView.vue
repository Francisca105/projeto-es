<template>
  <div>
    <v-card class="table">
      <v-card-title>
        <h2>Volunteer Profiles</h2>
      </v-card-title>
      <v-data-table :headers="headersVolunteerProfile" :items="volunteerProfiles" :search="search" disable-pagination
        :hide-default-footer="true" :mobile-breakpoint="0">
        <template v-slot:item.volunteer.creationDate="{ item }">
          {{ ISOtoString(item.volunteer.creationDate) }}
        </template>
        <template v-slot:item.volunteer.lastAccess="{ item }">
          {{ ISOtoString(item.volunteer.lastAccess) }}
        </template>
        <template v-slot:top>
          <v-card-title>
            <v-text-field v-model="search" append-icon="search" label="Search" class="mx-2" />
          </v-card-title>
        </template>
        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="viewProfile(item)">
                mdi-eye
              </v-icon>
            </template>
            <span>View Profile</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
    <!-- Institution Profiles -->
    <v-card class="table">
      <v-card-title>
        <h2>Institution Profiles</h2>
      </v-card-title>
      <v-data-table :headers="headersInstitutionProfile" :items="institutionProfiles" :search="search"
        disable-pagination :hide-default-footer="true" :mobile-breakpoint="0">
        <template v-slot:item.institution.creationDate="{ item }">
          {{ ISOtoString(item.institution.creationDate) }}
        </template>
        <template v-slot:top>
          <v-card-title>
            <v-text-field v-model="search" append-icon="search" label="Search" class="mx-2" />
          </v-card-title>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ISOtoString } from "../../services/ConvertDateService";
import RemoteServices from '@/services/RemoteServices';
import VolunteerProfile from '@/models/volunteer/VolunteerProfile';

@Component({
  methods: { ISOtoString }
})
export default class ProfilesListView extends Vue {
  volunteerProfiles: VolunteerProfile[] = []; // this is the object that will be used to fill in the table
  //institutionProfiles: InstitutionProfile[] = []; // TODO: this is the object that will be used to fill in the table

  search: string = '';

  headersVolunteerProfile: object = [
    { text: 'Name', value: 'volunteer.name', align: 'left', width: '10%' },
    {
      text: 'Short Bio',
      value: 'shortBio',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Registration Date',
      value: 'volunteer.creationDate',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Last Access',
      value: 'volunteer.lastAccess',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  headersInstitutionProfile: object = [
    { text: 'Name', value: 'institution.name', align: 'left', width: '10%' },
    {
      text: 'Short Description',
      value: 'shortDescription',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Registration Date',
      value: 'institution.creationDate',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Active',
      value: 'institution.active',
      align: 'left',
      width: '10%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.volunteerProfiles = await RemoteServices.getAllVolunteerProfiles();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  viewProfile(profile: VolunteerProfile) {
    const volunteerId = profile.volunteer?.id;

    if (!volunteerId) {
      this.$store.dispatch('error', 'No volunteer ID found');
      return;
    }

    this.$router.push({
      name: 'volunteer-profile',
      params: { id: volunteerId.toString() }
    });
  }
}
</script>

<style lang="scss" scoped>
.v-icon {
  cursor: pointer;
  transition: color 0.3s, transform 0.2s;

  &:hover {
    color: #1976D2;
    transform: scale(1.1);
  }

  &:active {
    transform: scale(0.9); // Efeito de clique
  }
}
</style>