describe('VolunteerProfile', () => {
    beforeEach(() => {
      cy.deleteAllButArs();
      cy.createDemoEntities();
      // cy.createDatabaseInfoForVolunteerAssessments()
      // cy.createDatabaseInfoForParticipations()
    });
  
    afterEach(() => {
      cy.deleteAllButArs();
    });

    
    it('creates volunteer profile without participations', () => {
      cy.demoVolunteerLogin();
        const SHORT_BIO = 'Test short bio for Cypress';
  
      // Intercept profile API calls
      cy.intercept('GET', '/profiles/volunteer/2').as('getProfile');
      cy.intercept('POST', '/profiles/volunteer/2').as('createProfile');
  
      // Navigate to profile page
      cy.visit('/profiles/volunteer/2');
  
      // Verify initial state - no profile
      cy.get('[data-cy=userLoginButton]')
        .should('exist')
        .and('contain', 'CREATE MY PROFILE');
  
      // Open creation dialog
      cy.get('[data-cy=userLoginButton]').click();

      // Write short bio
      cy.get('[data-cy="volunteerProfileCreationShortBio"]').type(SHORT_BIO);
  
      // Save profile
      cy.get('.v-dialog').contains('SAVE').click();
  
      // Wait for API response
      cy.wait('@getProfile').its('response.statusCode').should('eq', 200);
  
      // Verify profile details
      cy.get('.text-description p')
        .should('contain', SHORT_BIO);
  
      // Verify empty stats
      const stats = ['Total Enrollments', 'Total Participations', 'Total Assessments', 'Average Rating'];
      stats.forEach((stat, index) => {
        cy.get('.stats-container .items').eq(index)
          .should('contain', stat)
          .and('contain', '0');
      });
  
      // Verify empty participations table
      cy.get('.v-data-table').should('not.exist');
      cy.logout();
    });


    it('create volunteer profile with participations', () => {
      cy.demoVolunteerLogin();
        // cy.createDatabaseInfoForAssessments();
      // cy.createDatabaseInfoForParticipations();
      cy.createDatabaseFullInfoForAssessments()

      const SHORT_BIO = 'Test short bio for Cypress';
  
      // Intercept profile API calls
      cy.intercept('GET', '/profiles/volunteer/2').as('getProfile');
      cy.intercept('POST', '/profiles/volunteer/2').as('createProfile');
  
      // Navigate to profile page
      cy.visit('/profiles/volunteer/2');
  
      // Verify initial state - no profile
      cy.get('[data-cy=userLoginButton]')
        .should('exist')
        .and('contain', 'CREATE MY PROFILE');
  
      // Open creation dialog
      cy.get('[data-cy=userLoginButton]').click();

      // Write short bio
      cy.get('[data-cy="volunteerProfileCreationShortBio"]').type(SHORT_BIO);

      cy.get('.v-data-table').should('contain', '★★★★★ 5/5');

      // Select the first participation
      cy.get('.v-data-table .v-data-table__checkbox').first().click();

      // Verify if it was selected (optional)
      cy.get('.v-data-table tr.v-data-table__selected').should('exist');
  
      // Save profile
      cy.get('.v-dialog').contains('SAVE').click();
  
      // Wait for API response
      cy.wait('@getProfile').its('response.statusCode').should('eq', 200);
  
      // Verify profile details
      cy.get('.text-description p')
        .should('contain', SHORT_BIO);
  
      // Verify stats
      const stats = ['Total Enrollments', 'Total Participations', 'Total Assessments', 'Average Rating'];
      const statsNumbers = ["4", "3", "0", "5"];
      stats.forEach((stat, index) => {
        cy.get('.stats-container .items').eq(index)
          .should('contain', stat)
          .and('contain', statsNumbers[index]);
      });
  
      // Verify participations table
      cy.get('.v-data-table').should('contain', 'A1');
      cy.get('.v-data-table').should('contain', 'A2');
      cy.get('.v-data-table').should('contain', 'A6');
      cy.logout();
    });


    it('get volunteer profile', () => {
      // Starts without any profiles
      cy.visit('/profiles/view');
  
      // Target the institution profiles table (second .table element)
      cy.get('.table').eq(0).within(() => {
        // Verify table headers
        cy.contains('th', 'Name');
        cy.contains('th', 'Short Bio');
        cy.contains('th', 'Registration Date');
        cy.contains('th', 'Last Access');
        cy.contains('th', 'Actions');

        // Verify no data message
        cy.contains('No data available').should('exist');
      });

      cy.createDatabaseInfoForVolunteerProfile()

      const SHORT_BIO = 'Test short bio for Cypress';
  
      // Intercept profile API calls
      cy.visit('/profiles/view');

      cy.get('.table').first().within(() => {
        cy.get('thead th').should('have.length', 5);
        cy.contains('th', 'Name');
        cy.contains('th', 'Short Bio');
        cy.contains('th', 'Registration Date');
        cy.contains('th', 'Last Access');
        cy.contains('th', 'Actions');

        // Verify table exists and has data
        cy.get('tbody tr').should('have.length.gt', 0);

        // Name (DEMO-VOLUNTEER)
        cy.get('td').eq(0).should('contain', 'DEMO-VOLUNTEER');
        
        // Short Bio (Test short bio for Cypress)
        cy.get('td').eq(1).should('contain', 'Test short bio for Cypress');
        
        // Registration Date (2022-02-06 17:58)
        cy.get('td').eq(2).should('contain', '2022-02-06');
        cy.get('td').eq(2).should('contain', '17:58');
        cy.get('.v-icon.mdi-eye').should('exist');
        
        // Click the eye icon in the first row's action cell
        cy.get('tbody tr:first-child .v-icon.mdi-eye').click();
      });
    
      // Verify navigation to profile view
      cy.url().should('include', `/profiles/volunteer/`);
      
      // Verify profile details
      cy.get('.text-description p')
        .should('contain', SHORT_BIO);
  
      // Verify stats
      const stats = ['Total Enrollments', 'Total Participations', 'Total Assessments', 'Average Rating'];
      stats.forEach((stat, index) => {
        cy.get('.stats-container .items').eq(index)
          .should('contain', stat)
          .and('contain', '0');
      });
    });
  });
