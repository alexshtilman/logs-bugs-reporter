/**
 * 
 */
package telran.logs.reactive.tests;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
public class CommonWebTests {

	enum Method {
		POST, PUT, GET
	}


	public static <T> void Send_and_expect_Fail(WebTestClient webClient, Method method, HttpStatus status, String uri,
			T invalidDto) {
		StatusAssertions responceStatus;
		switch (method) {
		case GET: {
			responceStatus = getResponceFromGet(webClient, uri).expectStatus();
		}
			break;
		case POST: {
			responceStatus = getResponceFromPost(webClient, uri, invalidDto).expectStatus();
		}
			break;
		case PUT: {
			responceStatus = getResponceFromPut(webClient, uri, invalidDto).expectStatus();
		}
			break;
		default: {
			throw new IllegalArgumentException("Unexpected value: " + method);
		}
		}

		switch (status) {
		case BAD_REQUEST: {
			responceStatus.isBadRequest();
			break;
		}
		case NOT_FOUND: {
			responceStatus.isNotFound();
			break;
		}
		case I_AM_A_TEAPOT: {
			responceStatus.is4xxClientError();
			break;
		}

		case CONFLICT: {
			responceStatus.is4xxClientError();
			break;
		}
		case INTERNAL_SERVER_ERROR: {
			responceStatus.is5xxServerError();
			break;
		}
		case UNPROCESSABLE_ENTITY: {
			responceStatus.is4xxClientError();
		}
		default: {
			throw new IllegalArgumentException("Unexpected value: " + status);

		}
		}
	}

	public static <T> void testGetOkAndEqual(WebTestClient webClient, String uri, Class<T> dtoResponceClazz,
			List<T> dtoResponce) {
		webClient.get().uri(uri).exchange().expectStatus().isOk().expectBodyList(dtoResponceClazz)
				.isEqualTo(dtoResponce);
	}

	public static <T> void testGetOkAndEqual(WebTestClient webClient, String uri, Class<T> dtoResponceClazz,
			T dtoResponce) {
		webClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(dtoResponceClazz).isEqualTo(dtoResponce);
	}

	public static <T> void testPostIsBadRequest(WebTestClient webClient, String uri, T invalidDto) {
		getResponceFromPost(webClient, uri, invalidDto).expectStatus().isBadRequest();
	}

	public static void testGetIsBadRequest(WebTestClient webClient, String uri) {
		webClient.get().uri(uri).exchange().expectStatus().isBadRequest();
	}

	public static <T> void testPutOk(WebTestClient webClient, String uri, T dtoRequest) {
		getResponceFromPut(webClient, uri, dtoRequest).expectStatus().isOk();
	}

	public static <T> void testPutOkAndEqual(WebTestClient webClient, String uri, Class<T> dtoResponceClazz,
			T dtoRequest) {
		getResponceFromPut(webClient, uri, dtoRequest).expectStatus().isOk().expectBody(dtoResponceClazz)
				.isEqualTo(dtoRequest);
	}

	public static <T> void testPutIsBadRequest(WebTestClient webClient, String uri, T dto) {
		getResponceFromPut(webClient, uri, dto).expectStatus().isBadRequest();
	}

	public static <T> void testPostOkAndEqual(WebTestClient webClient, String uri, Class<T> dtoResponceClazz,
			T dtoRequest) {
		expectOkAndEqualFromPost(webClient, uri, dtoRequest, dtoRequest, dtoResponceClazz);
	}

	public static <T, P> void testPostOkAndEqual(WebTestClient webClient, String uri, T dtoRequest, P dtoResponce,
			Class<P> dtoResponceClazz) {
		expectOkAndEqualFromPost(webClient, uri, dtoRequest, dtoResponce, dtoResponceClazz);
	}

	public static <T, P> void expectOkAndEqualFromPost(WebTestClient webClient, String uri, T dtoRequest, P dtoResponce,
			Class<P> dtoResponceClazz) {
		getResponceFromPost(webClient, uri, dtoRequest).expectStatus().isOk().expectBody(dtoResponceClazz)
				.isEqualTo(dtoResponce);
	}

	public static <T> ResponseSpec getResponceFromPost(WebTestClient webClient, String uri, T dtoRequest) {
		return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(dtoRequest).exchange();
	}

	public static <T> ResponseSpec getResponceFromPut(WebTestClient webClient, String uri, T dtoRequest) {
		return webClient.put().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(dtoRequest).exchange();
	}

	public static <T> ResponseSpec getResponceFromGet(WebTestClient webClient, String uri) {
		return webClient.get().uri(uri).exchange();
	}
}
