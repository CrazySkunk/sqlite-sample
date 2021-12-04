package uk.ac.gre.mf9669w.m_expense.retrofit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public interface IPostTrip {
    @POST("comp1424cw")
    Call<Trip> postTrip(@Body Trip trip);
}
