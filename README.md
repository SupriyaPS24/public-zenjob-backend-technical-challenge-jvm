# Zenjob Coding Challenge

This is a **backend coding challenge**, containing **three different tasks**. The repository consists of a simplified version of Zenjob's platform:

- *Companies* can order *jobs*
- Each *job* contains one or more *shifts*
- *Talents* (workers) can be booked for *shifts*

### No bootstrapping needed

To allow you to dive right into it, there is a simplified version of a job service provided, which already contains the following features:

- Creating a *job* with multiple shifts
- Fetching the *shifts* for a specific *job*
- Booking a *talent* to a *shift*

Feel free to adjust it as much as you like.

### Product boundary conditions

There are certain boundary conditions defined which **must** be met by the service.

- A *job* should have at least one *shift*
- The start date of a *job* cannot be in the past
- The end date of a *job* should be after the start date
- A *shift*'s length should equal to 8 hours

### Out of scope

In order to keep the scope reasonably sized, there is no possibility for a company to request jobs for specific times.

## Task A: Cancel a Job

**As a company, I can cancel a job I previously ordered. If the job gets cancelled, all of its shifts get cancelled as well.**

**Implementation:**
- **REST Call:** DELETE
- **Endpoint:** `/job/cancelJob/{jobId}`
- **Path Parameter:** `jobId` - The ID generated when creating the job.

**Approach:**
- Implemented a DELETE mapping REST call.
- Created a method `cancelJob` which cancels the job and all associated shifts based on the provided `jobId`.
- If the given `jobId` is invalid or no job is found, a 404 Not Found status is returned.
- If the job is cancelled, all associated shifts are also cancelled, and both the JOB_PROCESS and SHIFT tables are updated.

**Curl Command:**
```bash
curl --location --request DELETE 'http://localhost:8080/job/cancelJob/d54c194f-a9c2-44dd-8035-a1ef3e79a721'
#  (d54c194f-a9c2-44dd-8035-a1ef3e79a721 is the jobId that is requested to be cancelled)
```
## Task B: Cancel a Single Shift

**As a company, I can cancel a single shift of a job I ordered previously.**

### Implementation:
- **REST Call:** DELETE
- **Endpoint:** `/shift/cancelShift/{shiftId}`
- **Path Parameter:** `shiftId` - The ID generated when creating the shift.

**Approach:**
- Implemented a DELETE mapping REST call.
- Created a method `cancelShift` which cancels the particular shift based on the provided `shiftId`.
- If the given `shiftId` is invalid or no shift is found, a 404 Not Found status is returned.
- If a job has only one shift, or if the last shift of any job is cancelled, the job is also deleted along with the last shift to meet the boundary condition that a job should have at least one shift.
- This operation updates the SHIFT table and, for jobs with a single shift, updates both the JOB_PROCESS and SHIFT tables.

**Curl Command:**
```bash
curl --location --request DELETE 'http://localhost:8080/shift/cancelShift/404846cb-74da-402c-8358-da583696dc67'
# (404846cb-74da-402c-8358-da583696dc67 is the shiftId that is requested to be cancelled)
```
## Task C: Cancel Shifts for a Specific Talent

**As a company, I CAN cancel all of my shifts which were booked for a specific talent AND replacement shifts are created with the same dates.**

### Implementation
- **REST Call:** POST
- **Endpoint:** `/shift/cancelTalent/{talentId}`
- **Path Parameter:** `talentId` - The ID of the talent for whom shifts need to be cancelled.

**Approach:**
- Used PostMapping to create the endpoint.
- Created a method called `cancelShiftsForTalent` which takes the `talentId` as a parameter and searches for shifts associated with that talent.
- Cancels all the shifts associated with the provided `talentId` and replaces them with new shifts having the same dates, allowing them to be booked for other talents.
- This operation affects the SHIFT table.

### Implementation Details:
- The method first retrieves all shifts associated with the given talent.
- If no shifts are found, a 404 Not Found status is returned.
- If shifts are found, they are cancelled, and new shifts with the same dates are created and saved to the database.
- This ensures that the job requirements are still met while allowing the talent to be replaced.
- [Note: there is no request body for this POST mapping; however, if needed, the `talentId` can be included in the request body instead of as a request parameter.]

**Curl Command:**
```bash
curl --location --request POST 'http://localhost:8080/shift/cancelTalent/123e4567-e87c-1667-a906-456915174999'
# (123e4567-e87c-1667-a906-456915174999 is the talentId)
```
